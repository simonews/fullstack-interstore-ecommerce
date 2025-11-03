// src/api/auth.js
export async function login(username, password) {
  const response = await fetch("http://localhost:8080/api/auth/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ username, password }),
  });

  if (!response.ok) {
    throw new Error("Errore nel login");
  }

  return await response.json(); //ricezione token JWT
}
