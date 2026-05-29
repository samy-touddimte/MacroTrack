
interface SocialLinksProps {
  color?: 'dark' | 'white';
}

const SocialLinks = ({ color = 'dark' }: SocialLinksProps) => {
  const className = color === 'white' ? 'white' : '';
  const github = import.meta.env.VITE_GITHUB_URL ?? 'https://github.com';
  const linkedin = import.meta.env.VITE_LINKEDIN_URL ?? 'https://linkedin.com';
  const portfolio = import.meta.env.VITE_PORTFOLIO_URL ?? '#';
  const email = import.meta.env.VITE_EMAIL ?? 'contact@example.com';

  return (
    <div className="social-links">
      <a href={github} target="_blank" rel="noreferrer" className={`social-button ${className}`}>
        <span>GitHub</span>
      </a>
      <a href={linkedin} target="_blank" rel="noreferrer" className={`social-button ${className}`}>
        <span>LinkedIn</span>
      </a>
      <a href={portfolio} target="_blank" rel="noreferrer" className={`social-button ${className}`}>
        <span>Site web</span>
      </a>
      <a href={`mailto:${email}`} className={`social-button ${className}`}>
        <span>Email</span>
      </a>
    </div>
  );
};

export default SocialLinks;
