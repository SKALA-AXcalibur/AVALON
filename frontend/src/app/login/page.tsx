import { LoginProjectBox } from "@/components/login/LoginProjectBox";

const LoginPage = () => {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-b from-white to-gray-100">
      <header className="absolute top-8 right-8">
        <span className="font-playfair text-2xl font-semibold text-gray-700">
          AXcalibur
        </span>
      </header>
      <h1 className="font-playfair text-[128px] font-bold tracking-widest text-gray-800 mb-4">
        AVALON
      </h1>
      <LoginProjectBox />
    </div>
  );
};

export default LoginPage;
