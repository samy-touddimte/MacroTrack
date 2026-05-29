import { Link } from 'react-router-dom';
import PageTitle from '../components/PageTitle/PageTitle';

const NotFoundPage = () => {
  return (
    <main className="container section not-found-page">
      <PageTitle title="Page introuvable" subtitle="La page que vous cherchez n’existe pas." />
      <div className="card card-panel not-found-card">
        <p>Retournez à l’accueil ou vérifiez l’URL.</p>
        <Link to="/" className="no-underline">
          <button className="bg-transparent border border-black text-black font-display py-2 px-6 rounded-full hover:bg-black hover:text-white transition-colors cursor-pointer text-sm uppercase tracking-wide">
            Accueil
          </button>
        </Link>
      </div>
    </main>
  );
};

export default NotFoundPage;
