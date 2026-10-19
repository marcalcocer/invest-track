import { cn } from "@/lib/cn";

const VARIANTS = {
    success: "bg-success/10 text-success",
    danger: "bg-danger/10 text-danger",
    neutral: "bg-muted text-muted-foreground",
};

export default function Badge({ variant = "neutral", className, ...props }) {
    return (
        <span
            className={cn(
                "inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium tabular-nums",
                VARIANTS[variant],
                className,
            )}
            {...props}
        />
    );
}
