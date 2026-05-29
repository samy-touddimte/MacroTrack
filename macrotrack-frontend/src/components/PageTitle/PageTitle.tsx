interface PageTitleProps {
  title: string;
  subtitle?: string;
  dateSubtitle?: string;
}

const PageTitle = ({ title, subtitle, dateSubtitle }: PageTitleProps) => {
  return (
    <div className="mb-6 md:mb-10 text-left">
      {dateSubtitle && (
        <p className="uppercase text-sm text-text-muted tracking-widest m-0 mb-1 font-bold">
          {dateSubtitle}
        </p>
      )}
      <h1 className="font-display text-3xl md:text-[3.5rem] text-gray-dark my-0 leading-none uppercase">
        {title}
      </h1>
      {subtitle && <p className="mt-2 text-text-muted">{subtitle}</p>}
    </div>
  );
};

export default PageTitle;
