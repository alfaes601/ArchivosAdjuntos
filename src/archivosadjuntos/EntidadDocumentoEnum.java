/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archivosadjuntos;


public enum EntidadDocumentoEnum {

    honorarioscliente("honorarios_c"),
    arrendamientocliente("arrendamiento_c"),
    alquilercliente("alquiler_c"),
    ordenserviciocliente("ordenservicio_c"),
    cotizacioncliente("cotizacion_c"),
    pedidocliente("pedido_c"),
    remisioncliente("remision_c"),
    facturacliente("factura_c"),
    notadecreditocliente("nota_credito_c"),
    anticipocliente("anticipo_c"),
    enviocliente("envio_c"),
    
    requisicionproveedor("requisicion_p"),
    cotizacionproveedor("cotizacion_p"),
    pedidoproveedor("pedido_p"),
    remisionproveedor("remision_p"),
    compraproveedor("compra_p"),
    gastoproveedor("gasto_p"),
    notadecreditoproveedor("nota_credito_p"),
    anticipoproveedor("anticipo_p"),
    recepcionproveedor("recepcion_p"),
    solicitudproveedor("solicitud_p");
    
    private final String entidad;

    EntidadDocumentoEnum(String entidad) {
        this.entidad = entidad;
    }

    public String getEntidad() {
        return entidad;
    }
}
