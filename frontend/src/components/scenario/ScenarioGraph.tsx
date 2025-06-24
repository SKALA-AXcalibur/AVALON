"use client";
import { useEffect, useRef, useState } from "react";
import mermaid from "mermaid";

let isInitialized = false;

export const ScenarioGraph = ({ graph }: { graph: string }) => {
  const graphRef = useRef<HTMLDivElement>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!isInitialized) {
      mermaid.initialize({
        startOnLoad: false,
        theme: "neutral",
        securityLevel: "loose",
        flowchart: {
          curve: "basis",
        },
      });
      isInitialized = true;
    }
  }, []);

  useEffect(() => {
    if (!graph) return;

    const renderGraph = async () => {
      setIsLoading(true);

      try {
        const container = graphRef.current!;
        container.innerHTML = "";

        const graphId = `mermaid-${Date.now()}`;
        const { svg } = await mermaid.render(graphId, graph);

        container.innerHTML = svg;
      } catch (error) {
        console.error("Mermaid 렌더링 오류:", error);
      } finally {
        setIsLoading(false);
      }
    };

    renderGraph();
  }, [graph]);

  if (!graph) {
    return (
      <div className="bg-slate-50 border border-slate-200 rounded-lg p-6 mb-4">
        <div className="h-80 flex items-center justify-center text-slate-500 font-medium">
          No graph available
        </div>
      </div>
    );
  }

  return (
    <div className="bg-slate-50 border border-slate-200 rounded-lg p-6 mb-4">
      <div className="relative min-h-48">
        {isLoading && (
          <div className="absolute inset-0 flex items-center justify-center bg-white/80 rounded">
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 border-2 border-slate-300 border-t-slate-600 rounded-full animate-spin" />
              <span className="text-slate-600">Loading...</span>
            </div>
          </div>
        )}
        <div
          ref={graphRef}
          className="mermaid-wrapper flex justify-center text-center"
        />
      </div>
    </div>
  );
};
