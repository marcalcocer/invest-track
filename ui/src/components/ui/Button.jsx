import { cn } from "@/lib/cn";

const BASE =
    "inline-flex items-center justify-center gap-2 rounded-lg text-sm font-medium transition-colors " +
    "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50";

const VARIANTS = {
    primary: "bg-primary text-primary-foreground hover:bg-primary/90",
    outline: "border border-border bg-card text-foreground hover:bg-muted",
    ghost: "text-muted-foreground hover:bg-muted hover:text-foreground",
};

const SIZES = {
    sm: "h-8 px-3",
    md: "h-10 px-4",
    icon: "h-10 w-10",
};

export default function Button({ variant = "primary", size = "md", className, type = "button", ...props }) {
    return <button type={type} className={cn(BASE, VARIANTS[variant], SIZES[size], className)} {...props} />;
}
