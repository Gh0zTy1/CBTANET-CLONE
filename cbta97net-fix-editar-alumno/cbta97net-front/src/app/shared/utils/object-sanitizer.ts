export const normalize = (obj: any): any => {
  if (obj === null || typeof obj !== 'object') {
    if (obj === "" || obj === undefined) return null;
    return obj;
  }

  const result = Object.fromEntries(
    Object.entries(obj).map(([key, value]) => [key, normalize(value)])
  );

  // verificar si todos son null para devolver un null
  const allNull = Object.values(result).every(v => v === null);

  return allNull ? null : result;
};