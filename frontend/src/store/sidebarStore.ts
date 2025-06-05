import { create } from "zustand";

interface SidebarState {
  openIndex: number | null;
  setOpenIndex: (index: number | null) => void;
}

export const useSidebarStore = create<SidebarState>((set) => ({
  openIndex: 0,
  setOpenIndex: (index) => set({ openIndex: index }),
}));
