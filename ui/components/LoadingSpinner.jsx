export default function LoadingSpinner() {
  return (
    <div className="fixed inset-0 flex flex-col items-center justify-center z-50 bg-opacity-50">
      <div className="text-lg font-medium text-gray-700 dark:text-gray-300">
        Please wait, loading your content...
      </div>
      <div className="w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
    </div>
  );
}
