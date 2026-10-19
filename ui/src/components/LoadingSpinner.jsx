export default function LoadingSpinner() {
  return (
    <div
      role="status"
      className="fixed inset-0 flex flex-col items-center justify-center gap-3 z-50 bg-background/60 backdrop-blur-sm"
    >
      <div className="w-10 h-10 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
      <div className="text-sm font-medium text-muted-foreground">
        Please wait, loading your content...
      </div>
    </div>
  );
}
