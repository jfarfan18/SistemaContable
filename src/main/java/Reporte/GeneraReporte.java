package Reporte;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

/**
 *
 * @author vicastoc
 */
public class GeneraReporte {

    private HashMap<String, Object> parametros;
    private byte[] bytesReporte;

    /**
     * @return the bytesReporte
     */
    public byte[] getBytesReporte() {
        return bytesReporte;
    }

    /**
     * @param bytesReporte the bytesReporte to set
     */
    public void setBytesReporte(byte[] bytesReporte) {
        this.bytesReporte = bytesReporte;
    }

    /**
     *
     */
    public enum opcionesExportar {

        PDF, HTML, EXCEL, RTF, DOCX
    }

    /**
     *
     * @param directorioReporte
     * @param nombreReporte
     * @param nombreReportePdf
     * @param tipo
     * @param externalContext
     * @param facesContext
     */
    public void exporta(String directorioReporte, String nombreReporte, String nombreReportePdf, String tipo, ExternalContext externalContext, FacesContext facesContext) {
        try {
            Configuracion configuracion = new Configuracion();
            Connection conexion = DriverManager
          .getConnection("jdbc:mysql://localhost/SistemasContables?"
              + "user=root&password=1234");
            System.out.println("Pasoooo");
            configuracion.compilaReporte(externalContext, directorioReporte, nombreReporte);
            System.out.println("Pasoooo1");
            String reporteAbsoluto = externalContext.getRealPath(directorioReporte + nombreReporte + ".jasper");
            System.out.println("Pasoooo2");
            JasperPrint impresionJasper = configuracion.creaReporte(reporteAbsoluto, parametros, conexion);
            conexion.close();

            if (tipo.equals("PDF")) {
                this.exportaRepotePDF(nombreReportePdf, impresionJasper, externalContext, facesContext);
                System.out.println("Exportado");
            }else if (tipo.equals("XLS")) {
                System.out.println("Exportando EXCELs");
                this.exportReporteEXCEL(nombreReportePdf, impresionJasper, externalContext, facesContext);
            }
            
        } catch (Exception ex) {
            System.out.println("Error en exporta " + ex.toString());
        }
    }

    public void exportaBytes(String directorioReporte, String nombreReporte, String nombreReportePdf, String tipo, ExternalContext externalContext, FacesContext facesContext) {
        try {
            Configuracion configuracion = new Configuracion();
            Connection conexion = configuracion.obtieneConexionBD();
            configuracion.compilaReporte(externalContext, directorioReporte, nombreReporte);
            String reporteAbsoluto = externalContext.getRealPath(directorioReporte + nombreReporte + ".jasper");
            JasperPrint impresionJasper = configuracion.creaReporte(reporteAbsoluto, parametros, conexion);
            conexion.close();

            if (tipo.equals("PDF")) {
                bytesReporte = JasperExportManager.exportReportToPdf(impresionJasper);
            }
        } catch (Exception ex) {
        }
    }

    /**
     *
     * @param nombreReportePdf
     * @param impresionJasper
     * @param externalContext
     * @param facesContext
     * @throws JRException
     * @throws IOException
     */
    private void exportaRepotePDF(String nombreReportePdf, JasperPrint impresionJasper, ExternalContext externalContext, FacesContext facesContext) throws JRException, IOException {
        bytesReporte = JasperExportManager.exportReportToPdf(impresionJasper);
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        response.addHeader("Content-disposition", "attachment;filename=" + nombreReportePdf);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(impresionJasper, servletOutputStream);
        /*response.setContentLength(bytesReporte.length);  
         response.getOutputStream().write(bytesReporte);  
         response.setContentType("application/pdf");*/
        facesContext.responseComplete();
    }

    /**
     *
     * @param exportar
     * @param impresionJasper
     * @param salida
     * @throws JRException
     */
    private void exportaReporte(JRAbstractExporter exportar, JasperPrint impresionJasper, PrintWriter salida) throws JRException {
        exportar.setParameter(JRExporterParameter.JASPER_PRINT, impresionJasper);
        exportar.setParameter(JRExporterParameter.OUTPUT_WRITER, salida);
        exportar.exportReport();
    }

    /**
     *
     * @param impresionJasper
     * @param salida
     * @throws JRException
     */
    private void exportReporteXHML(JasperPrint impresionJasper, PrintWriter salida) throws JRException {
        JRHtmlExporter exportar = new JRHtmlExporter();        
        exportar.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
        exportar.setParameter(JRExporterParameter.OUTPUT_WRITER, salida);
        exportar.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exportar.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-9");
        //exportar.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/SampleReportJSF/servlets/image?image=");//SampleReportJSF is the name of the project

        exportaReporte(exportar, impresionJasper, salida);
    }

    /**
     *
     * @param impresionJasper
     * @param salida
     * @throws JRException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void exportReporteEXCEL(String nombreReporteExcel, JasperPrint impresionJasper, ExternalContext externalContext, FacesContext facesContext) throws JRException, FileNotFoundException, IOException {
        //ByteArrayOutputStream colocaSalida = new ByteArrayOutputStream();
        //OutputStream salidaArchivo = new FileOutputStream(new File("d:/output/JasperReport1.xls"));//make sure to have the directory. excel file will export here

        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();        
        OutputStream servletOutputStream = response.getOutputStream();
        
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();                
        JRXlsExporter exportarXLS = new JRXlsExporter();
        
        exportarXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, impresionJasper);
        exportarXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, arrayOutputStream);
        exportarXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
        exportarXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
        exportarXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exportarXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
        exportarXLS.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.FALSE);
        
        exportarXLS.exportReport();

        response.setHeader("Content-disposition", "attachment; filename="+nombreReporteExcel);
        response.setContentType("application/vnd.ms-excel");
        response.setContentLength(arrayOutputStream.toByteArray().length);
        servletOutputStream.write(arrayOutputStream.toByteArray());
        servletOutputStream.flush();
        servletOutputStream.close();
        
        facesContext.responseComplete();
    }

    /**
     * @return the parametros
     */
    public HashMap<String, Object> getParametros() {
        return parametros;
    }

    /**
     * @param parametros the parametros to set
     */
    public void setParametros(HashMap<String, Object> parametros) {
        this.parametros = parametros;
    }

}
