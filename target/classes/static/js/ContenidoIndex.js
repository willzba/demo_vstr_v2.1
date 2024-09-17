const carrusel = document.getElementById("miCarrusel");
const anterior = document.getElementById("anterior");
const siguiente = document.getElementById("siguiente");
let slideIndex = 0;

function mostrarSlide(n) {
    const slides = document.querySelectorAll(".slide");
    if (n >= slides.length) {
        slideIndex = 0;
    }
    if (n < 0) {
        slideIndex = slides.length - 1;
    }
    carrusel.style.transform = `translateX(-${slideIndex * 100}%)`;
}

anterior.addEventListener("click", () => {
    slideIndex--;
    mostrarSlide(slideIndex);
});

siguiente.addEventListener("click", () => {
    slideIndex++;
    mostrarSlide(slideIndex);
});

// Iniciar carrusel
mostrarSlide(slideIndex);
