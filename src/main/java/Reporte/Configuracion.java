package Reporte;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.sql.*;
import javax.faces.context.ExternalContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class Configuracion {

    /**
     * 
     * @param contexto
     * @param pathReporte 
     */
    private void compila(ExternalContext contexto, String pathReporte) {
        System.setProperty("jasper.reports.compile.temp", contexto.getRealPath(pathReporte));
    }

    /**
     * 
     * @param contexto
     * @param directorioReporte
     * @param nombreReporte
     * @return
     * @throws JRException 
     */
    public boolean compilaReporte(ExternalContext contexto, String directorioReporte, String nombreReporte) throws JRException {
        String reporteJasper = contexto.getRealPath(directorioReporte + nombreReporte + ".jasper");
        File archivoJasper = new File(reporteJasper);

        if (archivoJasper.exists()) {
            return true; 
        }
        try {            
            compila(contexto, directorioReporte);
            String archivoJRXML = reporteJasper.substring(0, reporteJasper.indexOf(".jasper")) + ".jrxml";
            JasperCompileManager.compileReportToFile(archivoJRXML);
            return true;
        } catch (JRException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 
     * @param reporte
     * @param parametros
     * @param conexionBD
     * @return
     * @throws JRException
     * @throws SQLException
     * @throws FileNotFoundException 
     */
    public JasperPrint creaReporte(String reporte, Map<String, Object> parametros, Connection conexionBD) throws JRException, SQLException, FileNotFoundException {
        JasperPrint impresionJasper = JasperFillManager.fillReport(new FileInputStream(reporte), parametros, conexionBD);        
        return impresionJasper;
    }
    
    
    /**
     * 
     * @param contexto
     * @param directorioReporte
     * @param archivoJasper
     * @return 
     */
    public String getPathArchivoJasper(ServletContext contexto, String directorioReporte, String archivoJasper) {
        return contexto.getRealPath(directorioReporte + archivoJasper);
    }  

    /**
     * 
     * @return
     * @throws NamingException
     * @throws SQLException 
     */
    public Connection obtieneConexionBD() throws NamingException, SQLException {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:/jboss/datasources/mks");        
        return ds.getConnection();
    }

}
