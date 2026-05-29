import PageTitle from '../components/PageTitle/PageTitle';
import SocialLinks from '../components/SocialLinks/SocialLinks';

const CreditsPage = () => {
  return (
    <main className="bg-white min-h-screen pb-24">
      <section className="container section max-w-[800px] mx-auto py-8 px-5 text-black">
        <PageTitle title="CRÉDITS" />

        {/* Introduction */}
        <div className="pl-6 border-l-4 border-primary mb-12 mt-8">
          <div className="text-[1.1rem] text-[#333333] leading-relaxed">
            <p className="m-0 mb-4">
              <strong>MacroTrack</strong> est un projet personnel qui me tient particulièrement à cœur. Je voulais commencer une sèche sans perdre le peu de muscle que j’avais réussi à obtenir — ce qui arrive facilement quand on calcule mal son déficit calorique.
            </p>
            <p className="m-0 mb-4">
              Pour cela, je devais être le plus scientifique et méthodique possible dans mon alimentation et mon entraînement, mais j’étais aussi trop fainéant pour recalculer à la main, chaque semaine, mon nouveau déficit calorique en fonction de ma perte de poids.
            </p>
            <p className="m-0 mb-4">
              Après des jours de recherche, je n’ai trouvé qu’une seule application sur le marché qui répondait pleinement à mes exigences, qui était à jour par rapport à la littérature scientifique dans ses méthodes, et qui faisait les calculs à ma place. Mais elle coûte 15 € par mois… et je ne suis qu’un pauvre étudiant.
            </p>
            <p className="m-0">
              C’est pourquoi j’ai conçu MacroTrack : une alternative ouverte, que j’espère tout aussi performante, mais sans la facturation mensuelle.
            </p>
          </div>
        </div>

        {/* L'algorithme */}
        <div className="card mb-12">
          <h2 className="font-display text-[1.6rem] tracking-wide mb-8 text-black">
            L'ALGORITHME MÉTABOLIQUE (EWMA)
          </h2>
          <div className="flex flex-col gap-8">
            {[
              {
                num: "1",
                title: "ESTIMATION INITIALE",
                desc: "Calcul du métabolisme de base (BMR) via les formules de Katch-McArdle ou Mifflin-St Jeor selon les données disponibles, ajusté par un multiplicateur d'activité (PAL)."
              },
              {
                num: "2",
                title: "LISSAGE DU POIDS DYNAMIQUE (EWMA)",
                desc: "Application d'une Moyenne Mobile Pondérée Exponentiellement dynamique (alpha adaptatif selon les jours sans pesée) avec filtrage des valeurs aberrantes."
              },
              {
                num: "3",
                title: "TDEE EMPIRIQUE & DENSITÉ ÉNERGÉTIQUE",
                desc: "Calcul du métabolisme réel basé sur l'apport calorique et la perte de poids, ajusté selon une densité énergétique dynamique (ratio perte de muscle vs graisse)."
              },
              {
                num: "4",
                title: "ADAPTATIONS MÉTABOLIQUES",
                desc: "Modélisation avancée des freins physiologiques : Thermogenèse Adaptative progressive en déficit et régulation du métabolisme (NEAT) en surplus."
              },
              {
                num: "5",
                title: "PROJECTIONS & RECOMMANDATIONS",
                desc: "Ajustement continu de la cible calorique avec l'intégration de planchers de sécurité scientifique et la génération de scénarios de trajectoire."
              }
            ].map((step, idx) => (
              <div key={idx} className="flex gap-6 items-start">
                <div className="w-10 h-10 min-w-10 rounded-full bg-primary text-white flex items-center justify-center font-display text-xl">
                  {step.num}
                </div>
                <div>
                  <h3 className="font-display text-[1.15rem] text-black m-0 mb-1 tracking-wide">
                    {step.title}
                  </h3>
                  <p className="m-0 text-[0.9rem] text-[#555555] leading-normal">
                    {step.desc}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Stack technique */}
        <div className="stack-grid mb-14">
          {/* Backend */}
          <div className="card flex flex-col">
            <h3 className="font-display text-[1.3rem] tracking-wide mb-5 text-primary">
              ARCHITECTURE BACK-END
            </h3>
            <div className="flex flex-wrap gap-2">
              {["Java 21", "Spring Boot 3", "Spring Security", "JWT Authentication", "PostgreSQL", "Flyway Migrations", "MapStruct", "Caffeine Cache", "Bucket4j"].map((tech, idx) => (
                <span key={idx} className="badge">
                  {tech}
                </span>
              ))}
            </div>
          </div>

          {/* Frontend */}
          <div className="card flex flex-col">
            <h3 className="font-display text-[1.3rem] tracking-wide mb-5 text-primary">
              ARCHITECTURE FRONT-END
            </h3>
            <div className="flex flex-wrap gap-2">
              {["React 18", "TypeScript", "Vite.js", "Tailwind CSS", "React Query", "React Router", "Recharts", "React Hook Form"].map((tech, idx) => (
                <span key={idx} className="badge">
                  {tech}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Auteur */}
        <div className="bg-primary rounded-2xl flex flex-col items-center gap-4 text-center py-12 px-8 text-white">
          <span className="text-xs text-white/80 uppercase tracking-[0.15em] font-bold">
            RÉALISÉ PAR
          </span>
          <h2 className="font-display text-[2.5rem] text-white m-0 tracking-wide">
            SAMY TOUDDIMTE
          </h2>
          <p className="m-0 text-[0.95rem] text-white/90 max-w-[500px] leading-relaxed">
            Étudiant passionné par la conception logicielle, l'ergonomie web et la modélisation algorithmique.
          </p>

          <div className="my-2">
            <SocialLinks color="white" />
          </div>

          <p className="m-0 text-xs text-white/70 italic">
            Inspiré de l'application <a href="https://macrofactorapp.com/" target="_blank" rel="noopener noreferrer" className="text-white underline font-bold">MacroFactor</a>
          </p>
        </div>
      </section>
    </main>
  );
};

export default CreditsPage;
