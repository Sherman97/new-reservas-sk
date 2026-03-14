# language: es
Característica: Carrito de compras en OpenCart

  Escenario: Agregar producto destacado al carrito
    Dado que el usuario abre la home de OpenCart
    Cuando el usuario agrega el producto destacado "iPhone" al carrito
    Entonces debe mostrarse una alerta de exito
    Y el carrito debe contener el producto "iPhone"
