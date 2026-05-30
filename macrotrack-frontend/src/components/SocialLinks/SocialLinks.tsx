import { useState } from 'react';
import ContactModal from '../ContactModal/ContactModal';

interface SocialLinksProps {
  color?: 'dark' | 'white';
}

const SocialLinks = ({ color = 'dark' }: SocialLinksProps) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const className = color === 'white' ? 'white' : '';
  const github = import.meta.env.VITE_GITHUB_URL ?? 'https://github.com';
  const linkedin = import.meta.env.VITE_LINKEDIN_URL ?? 'https://linkedin.com';
  const portfolio = import.meta.env.VITE_PORTFOLIO_URL ?? '#';

  return (
    <>
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
        <button onClick={() => setIsModalOpen(true)} className={`social-button ${className} bg-transparent border-none p-0 cursor-pointer`}>
          <span>Email</span>
        </button>
      </div>

      <ContactModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
      />
    </>
  );
};

export default SocialLinks;
