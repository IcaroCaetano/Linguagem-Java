package org.lcr.process;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.compiere.model.MAttachment;
import org.compiere.model.MClient;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;


/**
 *	BlobToFile
 *
 *	Process to Export Binary Data of Date Base in File
 *	
 *	@author Icaro Caetano
 *	@version $Id: BlobToFile.java, v1.0 21/01/2015 08:29:00 icaro.caetano Exp $
 *  @version $Id: BlobToFile.java, v2.0 19/05/2016 17:29:00 bruno.sabino Exp $
 *  <li>http://ormel.com.br:9898/browse/IDCDENAD-865
 */
public class BlobToFile extends SvrProcess{
	
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(BlobToFile.class);

	/** Faixa de Ids da Imagem limite minimo	*/
	private static int lcr_LimitMin = 0;
	/** Faixa de Ids da Imagem limite máximo	*/
	private static int lcr_LimitMax = 0;
	/**	Path M_Attachment	*/
	private String m_attachmentPathRoot = "";
	/** attachment (root) path - if file system is used */
	private boolean isStoreAttachmentsOnFileSystem = false;
	// ICF - Competition control transactions
	//private Trx trx = Trx.get(Trx.createTrxName("PFA"), true);
	
	private int count = 1;
	
	/** Incremento						*/
	private int inc = 100;
	
	private int lcr_LimitMin_Cursor = 0;
	private int lcr_LimitMax_Cursor = 0;
	
	private Map<Integer, InputStream> mapsImg;
	
		
	private boolean last = false;
	
	protected void prepare()
    {
		ProcessInfoParameter[] para = getParameter();
		 
		for (int i = 0; i < para.length; i++){
			
		String name = para[i].getParameterName();
		if (para[i].getParameter() == null)    ;
        
		    else if(name.equals("lcr_LimitMin")){
        		lcr_LimitMin = para[i].getParameterAsInt();
				lcr_LimitMin_Cursor = lcr_LimitMin;}
        	else if(name.equals("lcr_LimitMax")){
        		lcr_LimitMax = para[i].getParameterAsInt();
        		lcr_LimitMax_Cursor = lcr_LimitMin_Cursor + inc;}
        		
        	else 
        		log.log(Level.SEVERE, "prepare   - Unknown Parameter: " + name);
		}
    }    //    prepare

	/**
     *  Perform process.
     *  @return Message (clear text)
     *  @throws Exception if not successful
     */
	protected String doIt() throws Exception
    {
		
		if( lcr_LimitMin > lcr_LimitMax)
			return "Intervalo Inválido! O valor Mín. é maior do que o valor Máx!";
		
		while(lcr_LimitMax_Cursor <= lcr_LimitMax){
    		
	    	StringBuffer sql = new StringBuffer("");
	    	sql.append(" SELECT AD_Attachment_ID, binarydata " +
	    			   " FROM AD_Attachment ");
	    	
	    	if (lcr_LimitMin >= 0 && lcr_LimitMax != 0){
	    		sql.append(	" WHERE (AD_Attachment_ID >= ? " +
	    					" AND AD_Attachment_ID <= ? )" +
	    					" AND title ilike 'zip' " +
	    					" AND BinaryData Is Not Null " +
	    					" ORDER BY AD_Attachment_ID" );
	    		
	    	}
	    	mapsImg = new HashMap<Integer,InputStream>();
	    	
	    	PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
				pstmt.setInt(1, lcr_LimitMin_Cursor);
				pstmt.setInt(2, lcr_LimitMax_Cursor);
				rs = pstmt.executeQuery();

				int it = 0;
					
				while (rs.next()) {
					mapsImg.put(rs.getInt(1),rs.getBinaryStream(2));
					it++;
					
					//se for a última faixa de IDs
					if(lcr_LimitMax_Cursor == lcr_LimitMax){
						if (rs.isLast())
							last = true;
					}
						
					if(lcr_LimitMax_Cursor < lcr_LimitMax){
						if (it == rs.getFetchSize())
							break;
						else
							if(lcr_LimitMax_Cursor == lcr_LimitMax){
								int temp = lcr_LimitMax_Cursor - lcr_LimitMin_Cursor;
								if(it==temp)
									break;
							}
					}
				}
				
			}
			
			catch (Exception e) {
				log.log(Level.SEVERE, sql.toString(), e);
			}
			finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				sql= null; 
			}
			
			if (!mapsImg.isEmpty()){
			
			Set<Integer> maps = mapsImg.keySet();
			
				for (Integer attachment_ID : maps){
					MAttachment attachment = new MAttachment(Env.getCtx(), attachment_ID, get_TrxName());
					InputStream input = mapsImg.get(attachment_ID);
					if (input != null)
						saveBlobToFile(attachment, input);
						log.warning(count++ + " - AD_Attachment: " + attachment.get_ID() + " - Table - " + attachment.getAD_Table_ID() 
							+ " - Record_ID - " + attachment.getRecord_ID());
				}
			}
			
			if (last)					
			return "*******************OK**********************" +
					"\n" +
					"Successfully onverted Images";
			else				
				{
					mapsImg = null;
					lcr_LimitMin_Cursor = lcr_LimitMax_Cursor + 1;
					lcr_LimitMax_Cursor = lcr_LimitMax_Cursor + inc;
					
					if(lcr_LimitMax_Cursor > lcr_LimitMax)
						lcr_LimitMax_Cursor = lcr_LimitMax;
					
					if(lcr_LimitMin_Cursor > lcr_LimitMax_Cursor)
						lcr_LimitMin_Cursor = lcr_LimitMax_Cursor;
				}
			
			
			if(lcr_LimitMin_Cursor == lcr_LimitMax_Cursor)
				return "*******************OK**********************" +
				"\n" +
				"Successfully converted Images";
			
	}
		// Qtd de Ids menor que o incremento
		if(lcr_LimitMax - lcr_LimitMin < inc){
			
			StringBuffer sql = new StringBuffer("");
	    	sql.append(" SELECT AD_Attachment_ID, binarydata " +
	    			   " FROM AD_Attachment ");
	    	
	    	if (lcr_LimitMin >= 0 && lcr_LimitMax != 0){
	    		sql.append(	" WHERE (AD_Attachment_ID >= ? " +
	    					" AND AD_Attachment_ID <= ? )" +
	    					" AND title ilike 'zip' " +
	    					" AND BinaryData Is Not Null " +
	    					" ORDER BY AD_Attachment_ID" );
	    		
	    	}
	    	Map<Integer, InputStream> mapsImg = new HashMap<Integer,InputStream>();
	    	
	    	PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			
			try {
				pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
				pstmt.setInt(1, lcr_LimitMin);
				pstmt.setInt(2, lcr_LimitMax);
				rs = pstmt.executeQuery();
				
				
				
				int it = 0;
				while (rs.next()) {
					mapsImg.put(rs.getInt(1),rs.getBinaryStream(2));
					it++;
					
					if (rs.isLast())
						last = true;
					
					if (it==lcr_LimitMax)
						break;
				}
			}
			
			catch (Exception e) {
				log.log(Level.SEVERE, sql.toString(), e);
			}
			finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
				sql= null; 
			}
			
			
			if (!mapsImg.isEmpty()){
							
				Set<Integer> maps = mapsImg.keySet();
			
				for (Integer attachment_ID : maps){
					MAttachment attachment = new MAttachment(Env.getCtx(), attachment_ID, get_TrxName());
					InputStream input = mapsImg.get(attachment_ID);
					if (input != null)
						saveBlobToFile(attachment, input);
						log.warning(count++ + " - AD_Attachment: " + attachment.get_ID() + " - Table - " + attachment.getAD_Table_ID() 
								+ " - Record_ID - " + attachment.getRecord_ID());
				}
			}
			
			if (last)
				return "*******************OK**********************" +
						"\n" +
						"Successfully converted Images";
		}
		
			    	
			return "*******************OK**********************" +
							"\n" +
							"Successfully converted Images";
	    			
		
    } // doIt
    

    
    public boolean isPDF(String m_name)
	{
		return m_name.toLowerCase().endsWith(".pdf");
	}
    
    
    public boolean isGraphic(String m_name)
	{
		String m_lowname = m_name.toLowerCase();
		return m_lowname.endsWith(".gif") || m_lowname.endsWith(".jpg") || m_lowname.endsWith(".png");
	}
    
   public String getExtension(String name){
	    String extensao = name.substring(name.lastIndexOf("."), name.length());  
	   return extensao;
   }
   
   /**
	 * 	Set Binary Data.
	 * 	Propagate to Lines/Taxes
	 *	@param ad_Attachment_Id int
	 *  @param xml String
	 */
	public void setBinaryData (int ad_Attachment_Id, String xml)
	{
	
		String set = "SET binarydata ='" + xml + "' WHERE AD_Attachment_ID = "
				+ ad_Attachment_Id;
		int noLine = DB.executeUpdateEx("UPDATE AD_Attachment " + set,
				get_TrxName());
		//log.fine("AD_Attachment_ID" + ad_Attachment_Id +  " - Lines=" + noLine);
	}	//	setBinaryData
	
	public void setTitle(int ad_Attachment_Id){
		String set = "SET title = 'xml' WHERE AD_Attachment_ID = "
				+ ad_Attachment_Id;
		int noLine = DB.executeUpdateEx("UPDATE AD_Attachment " + set,
				get_TrxName());
		//log.fine("AD_Attachment_ID" + ad_Attachment_Id + " - title - "
		//		+ "xml" + " - Lines=" + noLine);
	}
	
	public String getXml (String path, String name){
		String xml = "<entry file=\"%ATTACHMENT_FOLDER%"+path+name+"\" name=\""+name+"\"/>";
		return xml;		
	}
	
	public void saveBlobToFile(MAttachment attachment, InputStream input){
    	
		String pathRoot = "";

		MClient client = new MClient(Env.getCtx(),attachment.getAD_Client_ID(), get_TrxName());

		pathRoot =  getAttachmentPathRoot(client)
				+ attachment.getAD_Client_ID() + File.separator
				+ attachment.getAD_Org_ID() + File.separator
				+ attachment.getAD_Table_ID() + File.separator
				+ attachment.getRecord_ID() + File.separator;
		
		byte [] b = getByte(input);
		
		//create destination folder
		File destFile = new File(pathRoot);
		if(!destFile.exists()){
			if(!destFile.mkdirs()){
				;//log.warning("unable to create folder: " + destFile.getPath());
			}
		}
		
		String pathAbstract =	attachment.getAD_Client_ID() + File.separator
						+ attachment.getAD_Org_ID() + File.separator
						+ attachment.getAD_Table_ID() + File.separator
						+ attachment.getRecord_ID() + File.separator;;
	
		ByteArrayInputStream _in = new ByteArrayInputStream(b);
		ZipInputStream zinstream = new ZipInputStream(_in);
		ZipEntry zentry = null;
		byte[] buffer = new byte[1024];
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><attachments>";
		
		try {
			zentry = zinstream.getNextEntry();

			// Enquanto existir entradas no ZIP
			while (zentry != null) {
				// Pega o nome da entrada
				String entryName = zentry.getName();
				String newName = entryName.replace("'","");
				
				xml = xml + getXml(pathAbstract, newName);

				// Cria o output do arquivo , Sera extraido onde esta rodando a
				// classe
				FileOutputStream outstream = null;
				try {
					outstream = new FileOutputStream(pathRoot+newName);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int n;

				// Escreve no arquivo
				while ((n = zinstream.read(buffer)) > -1) {
					outstream.write(buffer, 0, n);
				}

				// Fecha arquivo
				outstream.close();

				// Fecha entrada e tenta pegar a proxima
				zinstream.closeEntry();
				zentry = zinstream.getNextEntry();
			}

			// Fecha o zip como um todo
			zinstream.close();
			xml = xml + "</attachments>";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		setBinaryData(attachment.get_ID(), xml);
		setTitle(attachment.get_ID());
 
	}
	
	/**
	 * Get the isStoreAttachmentsOnFileSystem and attachmentPath for the client.
	 * @author Icaro Caetano
	 * @param client MClient
	 */
	private String getAttachmentPathRoot(MClient client){
		isStoreAttachmentsOnFileSystem = client.isStoreAttachmentsOnFileSystem();
		if(isStoreAttachmentsOnFileSystem){
			if(File.separatorChar == '\\'){
				m_attachmentPathRoot = client.getWindowsAttachmentPath();
			} else {
				m_attachmentPathRoot = client.getUnixAttachmentPath();
			}
			if("".equals(m_attachmentPathRoot)){
				log.severe("no attachmentPath defined");
			} else if (!m_attachmentPathRoot.endsWith(File.separator)){
				log.warning("attachment path doesn't end with " + File.separator);
				m_attachmentPathRoot = m_attachmentPathRoot + File.separator;
				log.fine(m_attachmentPathRoot);
			}
		}
		return m_attachmentPathRoot;
	}
	
	/**
	 * @author icaro.caetano
	 * Convert InputStream to byte[]
	 * @param input
	 * @return byte[]
	 */
	public byte[] getByte(InputStream input){
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
		int nRead; 
		byte[] data = new byte[16384]; 
		try {
			while ((nRead = input.read(data, 0, data.length)) != -1) { 
				buffer.write(data, 0, nRead); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		try {
			buffer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return buffer.toByteArray();
	}
	
}
