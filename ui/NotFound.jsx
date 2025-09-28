export default function NotFound() {
    return (
        <div className="min-h-screen flex flex-col justify-center items-center bg-gray-100">
            <h1 className="text-5xl font-bold text-red-600">404</h1>
            <p className="text-xl mt-4 text-gray-700">Page not found</p>
            <a
                href="/"
                className="mt-6 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
                Go to Home
            </a>
        </div>
    );
}
