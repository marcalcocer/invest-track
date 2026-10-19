export default function NotFound() {
    return (
        <div className="min-h-screen flex flex-col justify-center items-center bg-background">
            <h1 className="text-5xl font-bold text-danger">404</h1>
            <p className="text-xl mt-4 text-muted-foreground">Page not found</p>
            <a
                href="/"
                className="mt-6 px-4 py-2 bg-primary text-primary-foreground rounded hover:opacity-90"
            >
                Go to Home
            </a>
        </div>
    );
}
