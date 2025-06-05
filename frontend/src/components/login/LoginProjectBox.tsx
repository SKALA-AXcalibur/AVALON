import DeleteProjectButton from "./DeleteProjectButton";
import LoginProjectButton from "./LoginProjectButton";
import ProjectIdInput from "./ProjectIdInput";

const LoginProjectBox = () => {
  return (
    <div className="bg-white shadow-lg rounded-xl p-8 w-[512px]">
      <form className="flex flex-col gap-4">
        <div className="flex gap-2 items-start">
          <ProjectIdInput />
          <LoginProjectButton />
        </div>
        <DeleteProjectButton />
      </form>
    </div>
  );
};

export default LoginProjectBox;
