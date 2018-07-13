/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archivosadjuntos;

/**
 *
 * @author esau
 */
public class ArchivoAdjuntoVO {
    
    private Integer idArchivoAdjunto;
    private byte[] contenido;
    private Integer idEntidad;
    private String mimeType;
    private Integer idEmpresa;
    private String urlRelativa;

    public ArchivoAdjuntoVO() {
    }

    public Integer getIdArchivoAdjunto() {
        return idArchivoAdjunto;
    }

    public void setIdArchivoAdjunto(Integer idArchivoAdjunto) {
        this.idArchivoAdjunto = idArchivoAdjunto;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }

    public Integer getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(Integer idEntidad) {
        this.idEntidad = idEntidad;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getUrlRelativa() {
        return urlRelativa;
    }

    public void setUrlRelativa(String urlRelativa) {
        this.urlRelativa = urlRelativa;
    }
    
}
