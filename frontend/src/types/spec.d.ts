type specFiles = {
  requirementFile: File;
  interfaceDef: File;
  interfaceDesign: File;
  databaseDesign: File;
};

type uploadSpecRequest = specFiles;

export type { specFiles, uploadSpecRequest };
