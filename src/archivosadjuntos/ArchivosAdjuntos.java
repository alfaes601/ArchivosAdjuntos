/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archivosadjuntos;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author esau
 */
public class ArchivosAdjuntos {

    static int RANGOBUSQUEDA = 500;
    static String PERFIL;
    static String BBDD;
    static String PASSWORD;
    static DAOArchivosAdjuntos daoArchivos = null;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if(args.length < 3){
            System.err.println("Se requieren parametros 1.-Perfil 2.-Base 3.-Pwd");
            return;
        }
        
        PERFIL = args[0];
        BBDD = args[1];
        PASSWORD = args[2];
        
        switch(PERFIL){
            case "desarrollo":
            case "producción":
                break;
            default:
                System.err.println("Perfil no válido");
                return;
        }
        
        try{
            DriverManager.registerDriver(new org.postgresql.Driver());
        }catch(SQLException e){
            System.err.println("Error al registrar el driver: "+e.getMessage());
        }
        daoArchivos = new DAOArchivosAdjuntos(BBDD, PASSWORD);
        
        ArchivosAdjuntos.crearArchivos("articulo");
        ArchivosAdjuntos.crearArchivos("cliente");
        ArchivosAdjuntos.crearArchivos("beneficiario");
        ArchivosAdjuntos.crearArchivos("proveedor");

        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.honorarioscliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.arrendamientocliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.alquilercliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.ordenserviciocliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.cotizacioncliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.pedidocliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.remisioncliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.facturacliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.notadecreditocliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.anticipocliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.enviocliente.toString());

        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.enviocliente.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.requisicionproveedor.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.cotizacionproveedor.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.pedidoproveedor.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.remisionproveedor.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.compraproveedor.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.gastoproveedor.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.notadecreditoproveedor.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.anticipoproveedor.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.recepcionproveedor.toString());
        ArchivosAdjuntos.crearArchivos(EntidadDocumentoEnum.solicitudproveedor.toString());
        
        ArchivosAdjuntos.eliminarColumnas();
    }

    public static void testConnection() {
        String qry = "select si_id_m_articulo as id_entidad, vc_nombre from m_articulo";
        List<ArchivoAdjuntoVO> lista = daoArchivos.getArchivosAdjuntos(qry, new ArrayList<Integer>());
        for (ArchivoAdjuntoVO m : lista) {
            System.out.println(m.getIdEntidad());
            System.out.println("----------------------------------------------");
        }
    }
    
    public static void crearArchivos(String entidad) {

        System.out.println("Guardando archivos para la entidad "+entidad+" ...");
        List<Integer> ids;
        switch (entidad) {
            case "articulo":
                ids = daoArchivos.getIDsArchivoAdjunto(daoArchivos.getQueryIdsArchivosArticulo());
                break;
            case "cliente":
            case "proveedor":
            case "beneficiario":
                ids = daoArchivos.getIDsArchivoAdjunto(daoArchivos.getQueryIdsArchivosTercero(entidad));
                break;
            default:
                ids = daoArchivos.getIDsArchivoAdjunto(daoArchivos.getQueryIdsArchivosDocumentos(EntidadDocumentoEnum.valueOf(entidad).getEntidad()));
        }
        int contadorArchivosGuardados = 0;
        int contTemp = 0;

        List<ArchivoAdjuntoVO> lista;

        int cont;
        if (ids.size() > RANGOBUSQUEDA) {
            while (ids.size() > 0) {
                cont = 0;
                List<Integer> itemp = new ArrayList<>();
                for (Iterator<Integer> it = ids.iterator(); it.hasNext() && cont < RANGOBUSQUEDA; cont++) {
                    Integer i = it.next();
                    itemp.add(i);
                }
                ids.removeAll(itemp);

                switch (entidad) {
                    case "articulo":
                        lista = daoArchivos.getArchivosAdjuntos(daoArchivos.getQueryArchivosArticulo(), itemp);
                        break;
                    case "cliente":
                    case "proveedor":
                    case "beneficiario":
                        lista = daoArchivos.getArchivosAdjuntos(daoArchivos.getQueryArchivosTercero(entidad), itemp);
                        break;
                    default:
                        lista = daoArchivos.getArchivosAdjuntos(daoArchivos.getQueryArchivosDocumentos(EntidadDocumentoEnum.valueOf(entidad).getEntidad()), itemp);
                        break;
                }

                //guardado fisico del archivo adjunto
                contTemp = guardarArchivo(lista, entidad, "archivosAdjuntos");
                contadorArchivosGuardados = contadorArchivosGuardados + contTemp;

                //guardando la ruta del archivo adjunto
                switch (entidad) {
                    case "articulo":
                        daoArchivos.guardarUrlArchivo(lista, daoArchivos.getQryUpdateUrlArticulo());
                        break;
                    case "cliente":
                    case "proveedor":
                    case "beneficiario":
                        daoArchivos.guardarUrlArchivo(lista, daoArchivos.getQryUpdateUrlTercero(entidad));
                        break;
                    default:
                        daoArchivos.guardarUrlArchivo(lista, daoArchivos.getQueryArchivosDocumentos(EntidadDocumentoEnum.valueOf(entidad).getEntidad()));
                        break;
                }
            }
        } else {

            switch (entidad) {
                case "articulo":
                    lista = daoArchivos.getArchivosAdjuntos(daoArchivos.getQueryArchivosArticulo(), ids);
                    break;
                case "cliente":
                case "proveedor":
                case "beneficiario":
                    lista = daoArchivos.getArchivosAdjuntos(daoArchivos.getQueryArchivosTercero(entidad), ids);
                    break;
                default:
                    lista = daoArchivos.getArchivosAdjuntos(daoArchivos.getQueryArchivosDocumentos(EntidadDocumentoEnum.valueOf(entidad).getEntidad()), ids);
                    break;
            }

            //guardado fisico del archivo adjunto
            contTemp = guardarArchivo(lista, entidad, "archivosAdjuntos");
            contadorArchivosGuardados = contadorArchivosGuardados + contTemp;

            //guardando la ruta del archivo adjunto
            switch (entidad) {
                case "articulo":
                    daoArchivos.guardarUrlArchivo(lista, daoArchivos.getQryUpdateUrlArticulo());
                    break;
                case "cliente":
                case "proveedor":
                case "beneficiario":
                    daoArchivos.guardarUrlArchivo(lista, daoArchivos.getQryUpdateUrlTercero(entidad));
                    break;
                default:
                    daoArchivos.guardarUrlArchivo(lista, daoArchivos.getQryUpdateUrlDocumentos(EntidadDocumentoEnum.valueOf(entidad).getEntidad()));
                    break;
            }
        }
        System.out.println("Archivos guardados: " + contadorArchivosGuardados);
    }

    private static int guardarArchivo(List<ArchivoAdjuntoVO> lista, String entidad, String campo) {

        int contador = 0;
        for (ArchivoAdjuntoVO tmp : lista) {

            FileOutputStream fop = null;
            File file;
            try {

                // ej. ruta relativa guardada--> /gm3sImages/desarrollo/erp/645/articulo/archivosAdjuntos/25393/25393_123231412312.png
                StringBuilder rutaRelativa = new StringBuilder();
                rutaRelativa.append("/gm3sImages/").append(PERFIL).append("/").append(BBDD).append("/");
                rutaRelativa.append(tmp.getIdEmpresa()).append("/").append(entidad).append("/");
                rutaRelativa.append(campo).append("/").append(tmp.getIdEntidad()).append("/");

                // ej. nombre archivo -> "/var/webapp/gm3sImages/desarrollo/erp/645/articulo/archivosAdjuntos/25393/25393_123231412312.png"
                StringBuilder rutaFisica = new StringBuilder();
                rutaFisica.append("/var/webapp/gm3sImages/").append(PERFIL).append("/").append(BBDD).append("/");;
                rutaFisica.append(tmp.getIdEmpresa()).append("/").append(entidad).append("/");
                rutaFisica.append(campo).append("/").append(tmp.getIdEntidad()).append("/");

                file = new File(rutaFisica.toString());

                if (!file.canRead()) {
                    file.setReadable(true);
                }

                if (!file.canWrite()) {
                    file.setWritable(true);
                }
                
                if (!file.exists()) {
                    FileUtils.forceMkdirParent(file);
//                    System.out.println("Directorio: "+file.getCanonicalPath()+" creado: "+file.exists());
                }

                //nombre del archivo
                StringBuilder nombreArchivo = new StringBuilder();
                nombreArchivo.append(tmp.getIdEntidad()).append("_").append(new Date().getTime()).append(".").append(tmp.getMimeType().split("/")[1]);

                //ruta relativa
                rutaRelativa.append(nombreArchivo);
                tmp.setUrlRelativa(rutaRelativa.toString());

                //ruta fisica
                file = new File(rutaFisica.append(nombreArchivo).toString());
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }

                fop = new FileOutputStream(file);

                fop.write(tmp.getContenido());
                fop.flush();
                fop.close();
                contador++;
            } catch (Exception ex) {
                System.out.println("Error al guardar archivo: " +ex.getMessage() + " Causa: "+ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }
        return contador;
    }
    
    private static void eliminarColumnas(){
        System.out.println("Eliminando columnas ...");
        daoArchivos.eliminarColumnas("ALTER TABLE e_m_articulo_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE catalogos.e_m_proveedor_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE catalogos.e_m_cliente_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE catalogos.e_m_beneficiario_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_factura_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_honorarios_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_arrendamiento_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_remision_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_envio_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_alquiler_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_anticipo_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_cotizacion_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_ordenservicio_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_pedido_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_nota_credito_c_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_cotizacion_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_anticipo_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_gasto_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_nota_credito_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_pedido_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_recepcion_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_remision_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_requisicion_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_solicitud_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
        daoArchivos.eliminarColumnas("ALTER TABLE documentos.e_d_compra_p_m_archivo_adjunto DROP COLUMN vo_archivo, DROP COLUMN sc_mime_type_archivo");
    }
}
