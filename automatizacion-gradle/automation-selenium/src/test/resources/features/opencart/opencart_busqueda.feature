# language: es
Característica: Busqueda de productos en OpenCart

  Escenario: Buscar iPhone desde la home
    Dado que el usuario abre la home de OpenCart
    Cuando el usuario busca el producto "iPhone"
    Entonces la busqueda debe mostrar resultados para "iPhone"
