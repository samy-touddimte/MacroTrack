import { Component, ErrorInfo, ReactNode } from 'react';
import { Link } from 'react-router-dom';

interface Props { children: ReactNode; }
interface State { hasError: boolean; error: Error | null; }

class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('Erreur critique dans le rendu du composant React :', error, info);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="p-16 text-center font-body">
          <h2 className="font-display text-5xl">
            OUPS
          </h2>
          <p className="text-text-muted my-4 mb-8">
            quelque chose s'est mal passé : {this.state.error?.message}
          </p>
          <Link to="/dashboard" className="no-underline">
            <button
              onClick={() => this.setState({ hasError: false, error: null })}
              className="bg-black text-white border-none rounded-full py-3 px-8 cursor-pointer font-display text-base"
            >
              RETOUR AU DASHBOARD
            </button>
          </Link>
        </div>
      );
    }
    return this.props.children;
  }
}

export default ErrorBoundary;
