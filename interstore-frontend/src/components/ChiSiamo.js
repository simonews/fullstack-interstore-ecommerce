import React from "react";

function ChiSiamo() {
  return (
    <div
      className="d-flex flex-column align-items-center text-center position-relative py-5"
      style={{
        backgroundImage: "url('/assets/background.jpg')",
        backgroundSize: "cover",
        backgroundPosition: "center",
        minHeight: "100vh", // 🔹 non fisso, ma minimo schermo intero
      }}
    >
      {/* Overlay */}
      <div
        className="position-absolute top-0 start-0 w-100 h-100"
        style={{ backgroundColor: "rgba(0,0,0,0.6)" }}
      ></div>

      {/* Contenuto sopra overlay */}
      <div
        className="position-relative text-white px-3"
        style={{ maxWidth: "900px", marginTop: "80px" }} // margine per non finire sotto navbar
      >
        <h1 className="mb-4">La Storia dell'Inter</h1>
        <p className="fs-6" style={{ lineHeight: "1.6" }}>
          L’Internazionale Milano, meglio conosciuta come Inter, nasce il 9 marzo 1908 da una scissione con il Milan.
          Un gruppo di soci, in disaccordo con la linea che voleva limitare la presenza di stranieri in squadra,
          decide di fondare un nuovo club, basato proprio sull’internazionalità, da cui deriva il nome. I colori scelti
          furono il nero e l’azzurro, simbolo del cielo notturno sopra la città.
          <br /><br />
          Sin dagli inizi, l’Inter si distingue nel panorama calcistico italiano, vincendo il suo primo scudetto già
          nel 1910. Nel corso dei decenni successivi, il club consolida la sua identità, diventando una delle squadre
          più amate e seguite. Gli anni ’30 vedono l’affermazione della società, che in quel periodo cambia
          temporaneamente nome in “Ambrosiana-Inter”, conquistando diversi titoli nazionali.
          <br /><br />
          Il dopoguerra apre una nuova fase: negli anni ’60 l’Inter di Angelo Moratti e dell’allenatore Helenio Herrera
          dà vita alla leggendaria “Grande Inter”. Con campioni come Facchetti, Mazzola e Suarez, la squadra domina in
          Italia e in Europa, vincendo scudetti e soprattutto due Coppe dei Campioni consecutive (1964 e 1965), oltre a
          due Coppe Intercontinentali.
          <br /><br />
          Dopo un periodo altalenante, gli anni ’80 e ’90 vedono nuovi protagonisti: da Altobelli a Matthäus, passando
          per Brehme, Bergomi e Zenga. Nel 1989 arriva lo scudetto dei record con Giovanni Trapattoni. Negli anni
          successivi il club si arricchisce di stelle come Ronaldo “il Fenomeno”, simbolo di un’Inter spettacolare ma
          spesso sfortunata.
          <br /><br />
          Il punto più alto della storia recente si raggiunge nel 2010, con la presidenza di Massimo Moratti e
          l’allenatore José Mourinho. L’Inter diventa la prima squadra italiana a conquistare il “Triplete”: scudetto,
          Coppa Italia e Champions League, quest’ultima vinta a Madrid contro il Bayern Monaco.
          <br /><br />
          Negli ultimi anni, con la nuova proprietà cinese di Suning e figure come Antonio Conte e Simone Inzaghi in
          panchina, l’Inter è tornata protagonista, vincendo lo scudetto del 2021 e raggiungendo la finale di Champions
          nel 2023 e nel 2025.
          <br /><br />
          Oggi l’Inter rappresenta una delle realtà più gloriose del calcio mondiale: una società che ha saputo unire
          tradizione e modernità, con una storia fatta di vittorie, campioni leggendari e una tifoseria appassionata che
          continua a riempire San Siro di nerazzurro.
        </p>
      </div>
    </div>
  );
}

export default ChiSiamo;
