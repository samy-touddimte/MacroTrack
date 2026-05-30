import { useState } from 'react';
import { useForm } from 'react-hook-form';
import emailjs from '@emailjs/browser';

interface ContactModalProps {
  isOpen: boolean;
  onClose: () => void;
}

interface ContactForm {
  user_name: string;
  user_email: string;
  message: string;
}

const ContactModal = ({ isOpen, onClose }: ContactModalProps) => {
  const [isSending, setIsSending] = useState(false);
  const [success, setSuccess] = useState(false);
  const [globalError, setGlobalError] = useState<string | null>(null);

  const { register, handleSubmit, formState: { errors }, reset } = useForm<ContactForm>();

  if (!isOpen) return null;

  const onSubmit = (data: ContactForm) => {
    setIsSending(true);
    setGlobalError(null);

    const serviceId = import.meta.env.VITE_EMAILJS_SERVICE_ID;
    const templateId = import.meta.env.VITE_EMAILJS_TEMPLATE_ID;
    const publicKey = import.meta.env.VITE_EMAILJS_PUBLIC_KEY;

    emailjs
      .send(serviceId, templateId, {
        user_name: data.user_name,
        user_email: data.user_email,
        message: data.message
      }, {
        publicKey: publicKey,
      })
      .then(
        () => {
          setIsSending(false);
          setSuccess(true);
          reset();
          setTimeout(() => {
            onClose();
            setSuccess(false); // reset for next time
          }, 2000);
        },
        (err) => {
          setIsSending(false);
          setGlobalError("Une erreur est survenue lors de l'envoi du message.");
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
        {!success && (
          <div className="flex justify-between items-center">
            <h2 className="font-display text-[2rem] text-black m-0 tracking-wide">
              ME CONTACTER
            </h2>
          </div>
        )}

        {success ? (
          <div className="flex flex-col items-center justify-center py-8 gap-3 text-center">
            <h2 className="font-display text-[2.5rem] text-black m-0 tracking-wide">
              MERCI !
            </h2>
            <div className="font-body text-base text-black">
              Votre message a bien été envoyé.<br/>Je vous répondrai dans les plus brefs délais.
            </div>
          </div>
        ) : (
          <form onSubmit={handleSubmit(onSubmit)} noValidate className="flex flex-col gap-5">
            <div>
              <label className="font-body text-[0.8rem] font-medium text-black uppercase tracking-wider block mb-2">
                NOM
              </label>
              <div className="custom-input-container">
                <input
                  type="text"
                  placeholder="Votre nom"
                  className="w-full border-none bg-transparent text-black text-base outline-none p-0 font-body"
                  {...register('user_name', { required: "Veuillez renseigner ce champ." })}
                />
              </div>
              {errors.user_name && (
                <div className="bg-[#FEF2F2] border-l-[3px] rounded-md p-2 mt-2 font-body text-xs leading-[1.4] text-left" style={{ borderColor: 'var(--color-red)', color: 'var(--color-red)' }}>
                  {errors.user_name.message}
                </div>
              )}
            </div>

            <div>
              <label className="font-body text-[0.8rem] font-medium text-black uppercase tracking-wider block mb-2">
                EMAIL
              </label>
              <div className="custom-input-container">
                <input
                  type="email"
                  placeholder="votre@email.com"
                  className="w-full border-none bg-transparent text-black text-base outline-none p-0 font-body"
                  {...register('user_email', { 
                    required: "Veuillez renseigner ce champ.",
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: "Adresse email invalide."
                    }
                  })}
                />
              </div>
              {errors.user_email && (
                <div className="bg-[#FEF2F2] border-l-[3px] rounded-md p-2 mt-2 font-body text-xs leading-[1.4] text-left" style={{ borderColor: 'var(--color-red)', color: 'var(--color-red)' }}>
                  {errors.user_email.message}
                </div>
              )}
            </div>

            <div>
              <label className="font-body text-[0.8rem] font-medium text-black uppercase tracking-wider block mb-2">
                MESSAGE
              </label>
              <div className="custom-input-container !items-start" style={{ padding: '12px' }}>
                <textarea
                  placeholder="Votre message..."
                  rows={4}
                  className="w-full border-none bg-transparent text-black text-base outline-none p-0 font-body resize-none"
                  {...register('message', { required: "Veuillez renseigner ce champ." })}
                />
              </div>
              {errors.message && (
                <div className="bg-[#FEF2F2] border-l-[3px] rounded-md p-2 mt-2 font-body text-xs leading-[1.4] text-left" style={{ borderColor: 'var(--color-red)', color: 'var(--color-red)' }}>
                  {errors.message.message}
                </div>
              )}
            </div>

            {globalError && (
              <div className="bg-[#FEF2F2] border-l-[3px] rounded-md p-2.5 font-body text-xs leading-[1.4] text-left" style={{ borderColor: 'var(--color-red)', color: 'var(--color-red)' }}>
                {globalError}
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
