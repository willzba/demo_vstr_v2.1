const botonDesplegable = document.querySelector(".boton-desplegable");
const menuDesplegable = document.querySelector(".menu-desplegable");

botonDesplegable.addEventListener("click", function () {
    if (menuDesplegable.style.display === "block") {
        menuDesplegable.style.display = "none";
    } else {
        menuDesplegable.style.display = "block";
    }
});

document.addEventListener("click", function (e) {
    if (!menuDesplegable.contains(e.target) && e.target !== botonDesplegable) {
        menuDesplegable.style.display = "none";
    }
});

const opciones = document.querySelectorAll(".menu-desplegable a");

opciones.forEach(function (opcion) {
    opcion.addEventListener("mouseenter", function () {
        opcion.style.backgroundColor = "white";
        opcion.style.color = "#154360";
    });

    opcion.addEventListener("mouseleave", function () {
        opcion.style.backgroundColor = "";
        opcion.style.color = "";
    });
});
