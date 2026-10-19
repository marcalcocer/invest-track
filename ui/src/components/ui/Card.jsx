import { cn } from "@/lib/cn";

export function Card({ className, ...props }) {
    return (
        <div
            className={cn("rounded-token border border-border bg-card text-card-foreground shadow-sm", className)}
            {...props}
        />
    );
}

export function CardHeader({ className, ...props }) {
    return <div className={cn("flex items-center justify-between gap-2 px-5 pt-5", className)} {...props} />;
}

export function CardTitle({ className, ...props }) {
    return <h3 className={cn("text-sm font-medium text-muted-foreground", className)} {...props} />;
}

export function CardContent({ className, ...props }) {
    return <div className={cn("px-5 pb-5 pt-2", className)} {...props} />;
}
