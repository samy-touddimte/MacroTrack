import { useRef, useState } from 'react';
import emailjs from '@emailjs/browser';

interface ContactModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const ContactModal = ({ isOpen, onClose }: ContactModalProps) => {
  const form = useRef<HTMLFormElement>(null);
  const [isSending, setIsSending] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const sendEmail = (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.current) return;

    setIsSending(true);
    setError(null);

    const serviceId = import.meta.env.VITE_EMAILJS_SERVICE_ID;
    const templateId = import.meta.env.VITE_EMAILJS_TEMPLATE_ID;
    const publicKey = import.meta.env.VITE_EMAILJS_PUBLIC_KEY;

    emailjs
      .sendForm(serviceId, templateId, form.current, {
        publicKey: publicKey,
      })
      .then(
        () => {
          setIsSending(false);
          setSuccess(true);
          setTimeout(() => {
            onClose();
            setSuccess(false); // reset for next time
          }, 2000);
        },
        (err) => {
          setIsSending(false);
          setError("Une erreur est survenue lors de l'envoi du message.");
          console.error('FAILED...', err.text);
        }
      );
  };

  return (
    <div
      onClick={onClose}
      className="fixed inset-0 bg-black/50 backdrop-blur-md flex items-center justify-center z-[60] p-5"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-[480px] bg-white rounded-2xl p-[30px] max-h-[90vh] overflow-y-auto text-black shadow-[0_20px_40px_rgba(0,0,0,0.15)] flex flex-col gap-[20px]"
      >
        <div className="flex justify-between items-center">
          <h2 className="font-display text-[2rem] text-black m-0 tracking-wide">
            ME CONTACTER
          </h2>
        </div>

        {success ? (
          <div className="bg-[#ECFDF5] border-l-[3px] rounded-md p-4 text-center font-body text-sm leading-[1.4]" style={{ borderColor: 'var(--color-green)', color: 'var(--color-green)' }}>
            <strong>Merci !</strong> Votre message a bien été envoyé.
          </div>
        ) : (
          <form ref={form} onSubmit={sendEmail} className="flex flex-col gap-5">
            <div>
              <label className="font-body text-[0.8rem] font-medium text-black uppercase tracking-wider block mb-2">
                NOM
              </label>
              <div className="custom-input-container">
                <input
                  type="text"
                  name="user_name"
                  required
                  placeholder="Votre nom"
                  className="w-full border-none bg-transparent text-black text-base outline-none p-0 font-body"
                />
              </div>
            </div>

            <div>
              <label className="font-body text-[0.8rem] font-medium text-black uppercase tracking-wider block mb-2">
                EMAIL
              </label>
              <div className="custom-input-container">
                <input
                  type="email"
                  name="user_email"
                  required
                  placeholder="votre@email.com"
                  className="w-full border-none bg-transparent text-black text-base outline-none p-0 font-body"
                />
              </div>
            </div>

            <div>
              <label className="font-body text-[0.8rem] font-medium text-black uppercase tracking-wider block mb-2">
                MESSAGE
              </label>
              <div className="custom-input-container !items-start" style={{ padding: '12px' }}>
                <textarea
                  name="message"
                  required
                  placeholder="Votre message..."
                  rows={4}
                  className="w-full border-none bg-transparent text-black text-base outline-none p-0 font-body resize-none"
                />
              </div>
            </div>

            {error && (
              <div className="bg-[#FEF2F2] border-l-[3px] rounded-md p-2.5 font-body text-xs leading-[1.4] text-left" style={{ borderColor: 'var(--color-red)', color: 'var(--color-red)' }}>
                {error}
              </div>
            )}

            <div className="flex gap-3 mt-2">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 py-[0.8rem] px-4 rounded-full border-none bg-gray-light hover:bg-[#EEEEEE] text-black font-body font-medium text-[0.85rem] tracking-wide cursor-pointer transition-all duration-150 ease-in-out"
              >
                ANNULER
              </button>
              <button
                type="submit"
                disabled={isSending}
                className={"flex-1 py-[0.8rem] px-4 rounded-full border-none bg-primary text-white font-body font-medium text-[0.85rem] tracking-wide transition-all duration-150 ease-in-out " + (isSending ? "cursor-not-allowed opacity-40 pointer-events-none" : "cursor-pointer")}
              >
                {isSending ? 'ENVOI...' : 'ENVOYER'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default ContactModal;
