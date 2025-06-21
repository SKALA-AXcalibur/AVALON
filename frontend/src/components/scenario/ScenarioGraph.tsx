export const ScenarioGraph = ({ graph }: { graph: string }) => {
  return (
    <div className="bg-slate-50 border border-slate-200 rounded-lg p-6 mb-4">
      {/* mermaid 코드 기반 그래프 출력 */}
      {graph === "" ? (
        <div className="h-[350px] flex items-center justify-center text-center text-slate-500 font-bold">
          그래프가 생성되지 않았습니다.
        </div>
      ) : (
        <>{graph}</>
      )}
    </div>
  );
};
