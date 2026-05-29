import { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import SocialLinks from '../components/SocialLinks/SocialLinks';

const LogoPlaceholder = () => (
  <div className="logo-placeholder min-w-[200px] min-h-[200px] flex items-center justify-end w-full">
    <span className="hero-title text-black text-right text-2xl leading-tight normal-case">
    </span>
  </div>
);

const HomePage = () => {
  const navigate = useNavigate();
  const { user, isLoading } = useAuth();

  useEffect(() => {
    if (!isLoading && user) {
      navigate('/dashboard');
    }
  }, [user, isLoading, navigate]);

  if (isLoading) {
    return <div className="container">Chargement...</div>;
  }

  return (
    <main className="bg-white min-h-screen">
      {/* HERO SECTION */}
      <section className="home-hero min-h-[80vh] flex items-center">
        <div className="container">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div className="flex flex-col items-start text-left gap-8 py-12 lg:py-0 lg:pr-12">
              <h1 className="hero-title text-left overflow-visible">
                <span className="whitespace-nowrap">Le tracker de macros dynamique</span>{' '}
                <span className="whitespace-nowrap">et sans compromis.</span>
              </h1>
              <p className="hero-subtitle text-left max-w-none">
                MacroTrack analyse votre métabolisme réel pour calibrer dynamiquement votre plan nutritionnel.<br />
                Fini les approximations :<br />
                laissez notre algorithme s'adapter à vos progrès quotidiens.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <Link to="/auth/register">
                  <button className="button-save-objectif w-[240px] h-[60px] p-0 m-0 font-display text-xl font-bold uppercase tracking-wide bg-purple-500 text-white border-2 border-purple-500 rounded-full flex items-center justify-center whitespace-nowrap">
                    S'INSCRIRE
                  </button>
                </Link>
                <Link to="/auth/login">
                  <button className="button-save-objectif w-[240px] h-[60px] p-0 m-0 font-display text-xl font-bold uppercase tracking-wide bg-transparent text-black border-2 border-black rounded-full flex items-center justify-center whitespace-nowrap">
                    SE CONNECTER
                  </button>
                </Link>
              </div>
            </div>
            
            <div className="flex items-center justify-end min-h-[300px]">
              <LogoPlaceholder />
            </div>
          </div>
        </div>
      </section>

      {/* PILLARS GRID SECTION */}
      <section className="pillars-section">
        <div className="container">
          <div className="pillars-grid">
            <div className="pillar-card">
              <span className="pillar-number">01</span>
              <h3 className="pillar-title">Fini les approximations</h3>
              <p className="pillar-desc">
                MacroTrack utilise vos fluctuations de poids réelles et vos calories consommées pour dresser votre courbe métabolique exacte, éliminant toute estimation générique.
              </p>
            </div>

            <div className="pillar-card">
              <span className="pillar-number">02</span>
              <h3 className="pillar-title">Calibré à votre métabolisme</h3>
              <p className="pillar-desc">
                Votre corps s'adapte, MacroTrack aussi. Recevez chaque semaine des ajustements précis en fonction de vos dépenses et de la vitesse de progression que vous avez choisie.
              </p>
            </div>

            <div className="pillar-card">
              <span className="pillar-number">03</span>
              <h3 className="pillar-title">Suivi auto-adaptatif</h3>
              <p className="pillar-desc">
                Plus besoin de recalculer manuellement vos macros lors d'un plateau. Notre algorithme recalculateur intelligent prend le relais en continu pour relancer votre perte ou prise de poids.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* TIMELINE STEPS SECTION */}
      <section className="steps-section-new">
        <div className="container">
          <div className="steps-header">
            <h2 className="steps-section-title">Comment ça marche</h2>
            <p className="steps-section-desc">Atteignez vos objectifs physiques en 3 étapes simples et logiques.</p>
          </div>

          <div className="steps-timeline-grid">
            <div className="step-card-new">
              <span className="step-badge-new">1</span>
              <h3 className="step-title-new">Configurez votre profil</h3>
              <p className="step-desc-new">
                Renseignez vos mensurations, votre sexe et votre niveau d'activité initial pour poser vos bases de départ.
              </p>
            </div>

            <div className="step-card-new">
              <span className="step-badge-new">2</span>
              <h3 className="step-title-new">Renseignez vos apports</h3>
              <p className="step-desc-new">
                Notez quotidiennement votre poids du matin et vos calories consommées pour nourrir en temps réel notre moteur métabolique.
              </p>
            </div>

            <div className="step-card-new">
              <span className="step-badge-new">3</span>
              <h3 className="step-title-new">Suivez la recommandation</h3>
              <p className="step-desc-new">
                MacroTrack recalcule automatiquement vos apports optimaux pour contourner les plateaux et assurer votre réussite.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* FOOTER HARMONISÉ */}
      <footer className="page-footer">
        <div className="container">
          <div className="footer-content">
            <div className="footer-brand">MACROTRACK</div>
            <div className="flex items-center gap-8">

              <SocialLinks color="white" />
            </div>
          </div>
        </div>
      </footer>
    </main>
  );
};

export default HomePage;
