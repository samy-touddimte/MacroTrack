import { Route, Routes } from 'react-router-dom';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/Register/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import AnalyticsPage from './pages/AnalyticsPage';
import ObjectifPage from './pages/Objectif/ObjectifPage';
import ProfilPage from './pages/Profil/ProfilPage';
import CreditsPage from './pages/CreditsPage';
import NotFoundPage from './pages/NotFoundPage';
import ProtectedRoute from './routes/ProtectedRoute';

import ErrorBoundary from './components/ErrorBoundary/ErrorBoundary';

import AuthenticatedLayout from './layouts/AuthenticatedLayout';

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/auth/login" element={<LoginPage />} />
      <Route path="/auth/register" element={<RegisterPage />} />
      <Route path="/dashboard" element={<ProtectedRoute><ErrorBoundary><AuthenticatedLayout><DashboardPage /></AuthenticatedLayout></ErrorBoundary></ProtectedRoute>} />
      <Route path="/analytics" element={<ProtectedRoute><ErrorBoundary><AuthenticatedLayout><AnalyticsPage /></AuthenticatedLayout></ErrorBoundary></ProtectedRoute>} />
      <Route path="/objectif" element={<ProtectedRoute><ErrorBoundary><AuthenticatedLayout><ObjectifPage /></AuthenticatedLayout></ErrorBoundary></ProtectedRoute>} />
      <Route path="/profil" element={<ProtectedRoute><ErrorBoundary><AuthenticatedLayout><ProfilPage /></AuthenticatedLayout></ErrorBoundary></ProtectedRoute>} />
      <Route path="/credits" element={<AuthenticatedLayout><CreditsPage /></AuthenticatedLayout>} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

export default App;
