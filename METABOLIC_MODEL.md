# MacroTrack : Modèle Algorithmique & Métabolique Fondamental

Ce document présente les fondements scientifiques et algorithmiques du backend de MacroTrack. 
Le système est conçu non seulement pour suivre les calories, mais aussi pour modéliser dynamiquement le métabolisme humain, s'adapter aux changements physiologiques et projeter des trajectoires de poids réalistes en utilisant des données empiriques et les sciences de la nutrition établies.

---

## 1. Métabolisme de Base (BMR & TDEE Initial)

Lorsqu'un utilisateur s'inscrit, les données empiriques (aliments et poids enregistrés) sont insuffisantes pour calculer sa véritable Dépense Énergétique Journalière Totale (TDEE - Total Daily Energy Expenditure). Le système s'appuie sur des formules scientifiques statiques pour établir une base.

### 1.1 Taux Métabolique de Base (BMR)
Le système calcule l'énergie de base requise pour maintenir les fonctions vitales au repos. Il sélectionne la meilleure formule en fonction des données utilisateur disponibles :
- **Formule de Katch-McArdle** : Utilisée si le pourcentage de graisse corporelle de l'utilisateur est connu. C'est la plus précise car elle calcule le BMR en fonction de la Masse Maigre (LBM - Lean Body Mass).
- **Formule de Mifflin-St Jeor** : Utilisée par défaut. Elle se base sur l'Âge, la Taille, le Poids et le Sexe Biologique. (Pour le sexe biologique `AUTRE`, un modificateur de moyenne arithmétique est appliqué comme compromis pragmatique).

### 1.2 Multiplicateurs de Niveau d'Activité (PAL)
Le BMR calculé est multiplié par un Niveau d'Activité Physique (PAL - Physical Activity Level) pour obtenir le TDEE initial.
- Sédentaire : ~1.2
- Légèrement Actif : ~1.375
- Modérément Actif : ~1.55
- Très Actif : ~1.725
- Extrêmement Actif : ~1.9

*Pourquoi ce choix ?* Le TDEE statique est un point de départ standard, mais il est notoirement imprécis au niveau individuel. MacroTrack n'utilise ce TDEE statique que pour les premiers jours (ou comme solution de repli) jusqu'à ce que suffisamment de données empiriques soient recueillies.

---

## 2. Calcul du TDEE Empirique (Le Moteur Principal)

Une fois que l'utilisateur a enregistré au moins 5 à 7 jours de données de poids et de nutrition, MacroTrack passe à un modèle de **TDEE Empirique**. C'est le véritable solveur "boîte noire" de l'application.

### 2.1 L'Équation du Bilan Énergétique
Au lieu de deviner le métabolisme en fonction de l'âge et de la taille, nous calculons exactement ce que le métabolisme *doit* être, en nous basant sur ce qui est entré (calories) et ce qui s'est passé (changement de poids).
`TDEE_estimé = calories_quotidiennes_moyennes - (Δpoids_hebdomadaire * densité_énergétique / 7)`

### 2.2 Gestion des Données Manquantes
Les utilisateurs n'enregistrent pas toujours parfaitement.
- **Estimation des Jours Manquants** : Si un utilisateur manque un jour d'enregistrement, le système estime les calories manquantes en supposant qu'il a consommé 100% de son apport moyen des jours enregistrés dans cette période (`MISSING_DAYS_COEFF=1.0`).
- **Confiance des Données** : Si trop peu de jours sont enregistrés dans une semaine, le système mélange le TDEE empirique avec le TDEE statique basé sur le BMR, pondéré par un "score de confiance" pour éviter les sauts erratiques du TDEE dus à de mauvaises données.

### 2.3 Pondération Exponentielle (Lissage EWMA)
Le métabolisme ne fait pas des bonds de 500 calories du jour au lendemain. Pour éviter les fluctuations volatiles du TDEE causées par le poids de l'eau ou les repas copieux, le TDEE estimé est lissé à l'aide d'une Moyenne Mobile à Pondération Exponentielle (EWMA) avec un alpha (`α`) de 0.15.

---

## 3. Tendance de Poids & Lissage

Le poids brut sur la balance est très volatil (influencé par le sodium, les glucides, l'hydratation et la digestion). L'utilisation du poids brut pour les calculs entraîne des erreurs massives.

### 3.1 EWMA Dynamique
Le système maintient un "Poids de Tendance" (Trend Weight). Chaque nouvelle entrée sur la balance met à jour la tendance.
- Lissage de base `α = 0.1` (Signifiant que le poids sur la balance d'aujourd'hui ne tire la tendance que de 10%).
- **Gestion des Écarts** : Si un utilisateur ne se pèse pas pendant plusieurs jours, la prochaine pesée devrait avoir plus de poids. Nous utilisons un alpha dynamique : `α_dynamic = 1 - (1-α)^n` où `n` est le nombre de jours depuis la dernière entrée.
- **Rejet des Valeurs Aberrantes** : Si une entrée de poids dévie de plus de 5% de la tendance actuelle, elle est signalée comme aberrante (ex. erreur de balance) et exclue du lissage.

---

## 4. Adaptations Métaboliques

Le corps humain résiste activement aux changements de poids. MacroTrack modélise ces défenses physiologiques pour garder des projections réalistes.

### 4.1 Thermogenèse Adaptative (Perte de Poids)
En déficit calorique, le corps ralentit son métabolisme (le NEAT diminue, la consommation d'énergie des organes baisse).
- **Déclencheur** : La pénalité commence après que l'utilisateur a perdu 2% de son poids corporel initial.
- **Pénalité Maximale** : Le métabolisme peut chuter d'un maximum de 12.5% de son TDEE, atteint lorsqu'il perd 15% de son poids corporel.
- **Rampe Temporelle** : L'adaptation n'est pas instantanée. Elle s'accentue de manière exponentielle sur une période de 90 jours.

### 4.2 Régulation à la Hausse du NEAT (Prise de Poids)
En surplus calorique, le corps augmente inconsciemment la Thermogenèse Liée aux Activités Non Sportives (NEAT) — ex. gigoter, faire les cent pas, posture — pour brûler l'excès d'énergie.
- **Déclencheur** : Commence après 21 jours en surplus calorique constant.
- **Boost Maximal** : Le TDEE augmente jusqu'à 5.5%.
- **Rampe Temporelle** : S'accentue sur une période de 28 jours après l'apparition.

*Pourquoi ce choix ?* Les calculateurs standards supposent une perte de poids linéaire (ex. -500 kcal = -0.5kg/semaine pour toujours). C'est scientifiquement faux. En modélisant la thermogenèse adaptative, les projections de MacroTrack vont "s'aplatir" avec le temps, reflétant les plateaux du monde réel et évitant la frustration de l'utilisateur.

---

## 5. Densité Énergétique (La Composition du Poids)

Tout poids perdu ou gagné n'est pas identique. 1 kg de graisse contient ~7700 kcal, mais 1 kg de muscle ne contient que ~1500 kcal. La valeur calorique du changement de poids dépend de *quel* tissu change.

### 5.1 Scénarios de Perte de Poids
- **Avec Entraînement en Résistance** : Le muscle est préservé. Le poids perdu est majoritairement de la graisse (ex. 5% de masse maigre, 95% de masse grasse). La densité énergétique du poids perdu est très élevée : **~7315 kcal/kg**.
- **Sans Entraînement** : Plus de muscle est perdu avec la graisse (ex. 25% de masse maigre, 75% de masse grasse). La densité énergétique est plus faible : **~6910 kcal/kg**.
*Impact :* Un utilisateur qui ne soulève pas de poids perdra du poids sur la balance plus rapidement avec le même déficit exact qu'un utilisateur qui en soulève, mais sa composition corporelle sera moins bonne.

### 5.2 Scénarios de Prise de Poids (Prise de Masse Maigre)
Lors de la prise de poids, le but est de maximiser le muscle et minimiser la graisse.
- Le système calcule un **Plafond de Gain Maigre** basé sur le sexe biologique de l'utilisateur, son style d'entraînement et son expérience d'entraînement (Novice vs. Avancé).
- Si le surplus calorique dépasse la limite physiologique de synthèse musculaire, l'excès d'énergie est entièrement stocké sous forme de graisse (7700 kcal/kg), abaissant la densité énergétique globale du poids gagné.

---

## 6. Moteur de Projections & Scénarios

Le `WeightProjectionEngine` exécute une simulation jour par jour dans le futur pour prédire quand l'utilisateur atteindra son objectif.

### 6.1 La Boucle de Simulation
Pour un nombre maximum de jours (ex. 365), le moteur itère jour par jour :
1. Calcule le TDEE pour ce jour futur (en incluant la réduction du poids corporel et l'adaptation métabolique).
2. Calcule l'objectif calorique pour ce jour en fonction du rythme hebdomadaire souhaité par l'utilisateur.
3. Applique un "Plancher Scientifique" (ex. les femmes ne devraient pas descendre en dessous de ~1200 kcal/jour, les hommes ~1500 kcal/jour) pour éviter des recommandations dangereuses de niveau famine.
4. Calcule le changement de poids théorique basé sur la densité énergétique dynamique.
5. Ajoute des "Bonus de Phase" (comme des baisses rapides de poids d'eau initiales en déficit, ou des pics de glycogène en surplus) qui se détériorent exponentiellement sur les 14 premiers jours.

### 6.2 Génération Multi-Scénarios
Pour gérer les attentes, le Tableau de Bord génère trois projections :
- **Attendu** : Basé sur le rythme cible exact.
- **Agressif** : Un rythme plus rapide. Pour la perte de poids, cela atteint l'objectif plus tôt.
- **Conservateur** : Un rythme plus lent. Pour la perte de poids, cela prend plus de temps mais est plus facile à respecter.

---

## 7. Analytique de l'Adhérence

La constance est plus importante que la perfection. Le moteur d'adhérence évalue la façon dont l'utilisateur respecte ses objectifs.
- **Score** : Il calcule un score pondéré favorisant fortement les comportements récents (ex. 7 derniers jours = 40% de poids, 7 jours précédents = 35% de poids, jours plus anciens = 25% de poids).
- **Tolérance** : Atteindre exactement 2000 kcal est impossible. L'adhérence permet une tolérance flexible (ex. ± 10% de la cible, plafonnée entre une plage de calories absolue minimale et maximale).
- Les jours à zéro calorie (s'ils sont délibérément enregistrés comme des jeûnes) sont gérés de manière sécurisée sans casser les moyennes.
