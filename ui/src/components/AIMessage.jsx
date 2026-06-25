import { useState, useEffect, useRef } from 'react';

export default function AIMessage() {
  const [fullText, setFullText] = useState('');
  const [displayedText, setDisplayedText] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);
  const intervalRef = useRef(null);

  useEffect(() => {
    async function fetchMessage() {
      console.log('[AIMessage] Starting fetch at', new Date().toISOString());
      const startTime = Date.now();
      try {
        const res = await fetch('/ollama/api/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            prompt: 'Say hello and share one short interesting fact about investing. Start your response with "Hello!". Keep it under 15 words.',
            model: 'llama3.2:3b',
          }),
        });
        console.log('[AIMessage] Response status:', res.status, 'after', Date.now() - startTime, 'ms');
        if (!res.ok) throw new Error(`HTTP ${res.status}: ${res.statusText}`);
        const data = await res.json();
        console.log('[AIMessage] Response data:', data);
        console.log('[AIMessage] Response text:', data.response);
        setFullText(data.response);
      } catch (err) {
        console.error('[AIMessage] Fetch error:', err);
        setHasError(true);
      } finally {
        setIsLoading(false);
        console.log('[AIMessage] Done loading after', Date.now() - startTime, 'ms');
      }
    }
    fetchMessage();
  }, []);

  useEffect(() => {
    if (!fullText) return;
    let i = 0;
    intervalRef.current = setInterval(() => {
      setDisplayedText(fullText.slice(0, i + 1));
      i++;
      if (i >= fullText.length) {
        clearInterval(intervalRef.current);
        console.log('[AIMessage] Typing animation complete');
      }
    }, 25);
    return () => clearInterval(intervalRef.current);
  }, [fullText]);

  if (hasError) return null;

  if (isLoading) {
    return (
      <div className="flex justify-center gap-1.5 py-6">
        <span className="w-2 h-2 bg-stone-400 rounded-full animate-bounce [animation-delay:-0.3s]" />
        <span className="w-2 h-2 bg-stone-400 rounded-full animate-bounce [animation-delay:-0.15s]" />
        <span className="w-2 h-2 bg-stone-400 rounded-full animate-bounce" />
      </div>
    );
  }

  return (
    <div className="text-center py-6 px-4">
      <p className="text-lg text-stone-300 italic leading-relaxed">
        <span className="text-cyan-500">&ldquo;</span>
        {displayedText}
        {displayedText.length < fullText.length && (
          <span className="inline-block w-0.5 h-5 bg-cyan-400 ml-0.5 animate-pulse align-middle" />
        )}
        <span className="text-cyan-500">&rdquo;</span>
      </p>
    </div>
  );
}
