/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archivosadjuntos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author esau
 */
public class DAOArchivosAdjuntos {
    
    ConectionBD conectionBD;

    public DAOArchivosAdjuntos(String base, String password) {
        conectionBD = new ConectionBD(base, password);
    }
    
    public List<Integer> getIDsArchivoAdjunto(String qry){
        Statement st = null;
        ResultSet rs = null;
        Connection connection = null;
        List<Integer> lista = new ArrayList<>();
        try {
            connection = conectionBD.getConnection();
            st = connection.createStatement();
            rs = st.executeQuery(qry);
            while (rs.next()) {
                lista.add(rs.getInt("id_archivo_adjunto"));
            }
            rs.close();
            st.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("Error en getIDsArchivoAdjunto() "+ e.getMessage());
            System.err.println("Con query "+qry);
            e.printStackTrace();
        } finally {
            ConectionBD.closeConnection(connection);
        }
        return lista;
    }
    
    public List<ArchivoAdjuntoVO> getArchivosAdjuntos(String qry, List<Integer> ids) {
        Statement st = null;
        ResultSet rs = null;
        Connection connection = null;
        List<ArchivoAdjuntoVO> lista = new ArrayList<>();
        try {
            connection = conectionBD.getConnection();
            st = connection.createStatement();
            if (!ids.isEmpty()) {
                StringBuilder strIds = new StringBuilder(" where aa.si_id_m_archivo_adjunto in (");
                for (Integer tmp : ids) {
                    strIds.append(tmp);
                    strIds.append(",");
                }
                strIds = new StringBuilder(strIds.substring(0, strIds.length() - 1));
                strIds.append(")");
                qry += strIds.toString();
            }
            
            rs = st.executeQuery(qry);
            int cont = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                ArchivoAdjuntoVO archivo = new ArchivoAdjuntoVO();
                for (int i = 1; i <= cont; i++) {
                    archivo.setIdArchivoAdjunto(rs.getInt("id_archivo_adjunto"));
                    archivo.setIdEntidad(rs.getInt("id_entidad"));
                    archivo.setContenido(rs.getBytes("contenido"));
                    archivo.setMimeType(rs.getString("mime_type"));
                    archivo.setIdEmpresa(rs.getInt("id_empresa"));
                }
                lista.add(archivo);
            }
            rs.close();
            st.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("Error en getArchivosAdjuntos() "+ e.getMessage());
            System.err.println("Con Query "+ qry);
            e.printStackTrace();
        } finally {
            ConectionBD.closeConnection(connection);
        }
        return lista;
    }
    
    public void guardarUrlArchivo(List<ArchivoAdjuntoVO> lista, String qry){
        
        PreparedStatement ps = null;
        Connection connection = null;
        try {
            for (ArchivoAdjuntoVO aa : lista) {
                connection = conectionBD.getConnection();
                ps = connection.prepareStatement(qry);
                ps.setString(1, aa.getUrlRelativa());
                ps.setString(2, aa.getMimeType());
                ps.setInt(3, aa.getIdArchivoAdjunto());
                ps.execute();
                ps.close();
                connection.close();
            }
        } catch (Exception e) {
            System.out.println("Error en guardarUrlArchivo() "+ e.getMessage());
            System.err.println("Con query "+qry);
            e.printStackTrace();
        } finally {
            ConectionBD.closeConnection(connection);
        }
    }
    
    public void eliminarColumnas(String qry){
        Statement st = null;
        Connection connection = null;
        try {
            connection = conectionBD.getConnection();
            st = connection.createStatement();
            st.executeUpdate(qry);
        } catch (Exception e) {
            System.out.println("Error en eliminarColumnas() "+ e.getMessage());
            System.err.println("Con query "+qry);
            e.printStackTrace();
        } finally {
            ConectionBD.closeConnection(connection);
        }
    }
    
    public String getQueryIdsArchivosArticulo(){
        return "select si_id_m_archivo_adjunto as id_archivo_adjunto from e_m_articulo_m_archivo_adjunto";
    }
    
    public String getQueryIdsArchivosTercero(String entidad){
        return "select si_id_m_archivo_adjunto as id_archivo_adjunto from catalogos.e_m_" + entidad + "_m_archivo_adjunto";
    }
    
    public String getQueryIdsArchivosDocumentos(String entidad){
        return "select si_id_m_archivo_adjunto as id_archivo_adjunto from documentos.e_d_" + entidad + "_m_archivo_adjunto";
    }
    
    public String getQueryArchivosArticulo(){
        StringBuilder qry = new StringBuilder();
        qry.append("select aa.si_id_m_archivo_adjunto as id_archivo_adjunto, aa.si_id_m_articulo as id_entidad, ");
        qry.append("       aa.vo_archivo as contenido, aa.sc_mime_type_archivo as mime_type, a.si_id_m_empresa as id_empresa ");
        qry.append("from m_articulo a ");
        qry.append("join e_m_articulo_m_archivo_adjunto aa on aa.si_id_m_articulo = a.si_id_m_articulo ");
        return qry.toString();
    }
    
    public String getQueryArchivosTercero(String entidad){
        StringBuilder qry = new StringBuilder();
        qry.append("select aa.si_id_m_archivo_adjunto as id_archivo_adjunto, aa.si_id_m_").append(entidad).append(" as id_entidad, ");
        qry.append("       aa.vo_archivo as contenido, aa.sc_mime_type_archivo as mime_type, ent.si_id_m_empresa as id_empresa ");
        qry.append("from catalogos.m_").append(entidad).append(" ent ");
        qry.append("join catalogos.e_m_").append(entidad).append("_m_archivo_adjunto aa on aa.si_id_m_").append(entidad).append(" = ent.si_id_m_").append(entidad).append(" ");
        return qry.toString();
    }
    
    public String getQueryArchivosDocumentos(String entidad){
        StringBuilder qry = new StringBuilder();
        qry.append("select aa.si_id_m_archivo_adjunto as id_archivo_adjunto, aa.si_id_d_").append(entidad).append(" as id_entidad, ");
        qry.append("       aa.vo_archivo as contenido, aa.sc_mime_type_archivo as mime_type, ent.si_id_m_empresa as id_empresa ");
        qry.append("from documentos.d_").append(entidad).append(" ent ");
        qry.append("join documentos.e_d_").append(entidad).append("_m_archivo_adjunto aa on aa.si_id_d_").append(entidad).append(" = ent.si_id_d_").append(entidad).append(" ");
        return qry.toString();
    }
    
    public String getQryUpdateUrlArticulo(){
        String qry= "update e_m_articulo_m_archivo_adjunto set vx_url = ?, vx_mime_type_contenido = ? where si_id_m_archivo_adjunto = ?";
        return qry;
    }
    
    public String getQryUpdateUrlTercero(String entidad){
        StringBuilder str= new StringBuilder();
        str.append("update catalogos.e_m_").append(entidad).append("_m_archivo_adjunto set vx_url = ?, vx_mime_type_contenido = ? where si_id_m_archivo_adjunto = ?");
        return str.toString();
    }
    
    public String getQryUpdateUrlDocumentos(String entidad){
        StringBuilder str= new StringBuilder();
        str.append("update documentos.e_d_").append(entidad).append("_m_archivo_adjunto set vx_url = ?, vx_mime_type_contenido = ? where si_id_m_archivo_adjunto = ?");
        return str.toString();
    }
    
}
