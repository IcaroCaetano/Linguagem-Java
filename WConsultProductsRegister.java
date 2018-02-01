package org.adempiere.webui.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.component.Tab;
import org.adempiere.webui.component.Tabbox;
import org.adempiere.webui.component.Tabpanel;
import org.adempiere.webui.component.Tabpanels;
import org.adempiere.webui.component.Tabs;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.editor.WLocatorEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.window.FDialog;
import org.adempiere.webui.window.WPAttributeDialog;
import org.compiere.model.MAttributeSet;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MClient;
import org.compiere.model.MColumn;
import org.compiere.model.MLocatorLookup;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategory;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Div;
import org.adempiere.webui.component.Messagebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;

/**
 *  Consult Products Register
 *
 *  @author 	Icaro Caetano
 *  @version 	$Id: WConsultProductsRegister.java,v 1.0 22/09/2017 10:16:00 icaro.caetano Exp $
 *  <li> JIRA [IDCDENAD-1756]
 *  <li> http://ormel.com.br:9898/browse/IDCDENAD-1756
 */
public class WConsultProductsRegister extends ADForm
	implements IFormController, EventListener, WTableModelListener, ValueChangeListener
{
	
	private static final long serialVersionUID = -5322824600164192235L;
	
	private CustomForm form = new CustomForm();
	
	public WConsultProductsRegister(){
		try
		{
			dynInit();
			
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}
	}
	
	/**	Window No			**/
	private int         m_WindowNo = 0;
	
	/**	Logger				**/
	private static CLogger log = CLogger.getCLogger(WConsultProductsRegister.class);
	
	private Panel southPanel = new Panel();
	
	/** UI					**/
	private Tabbox 		tabbox = new Tabbox();
	private Tabs 		tabs = new Tabs();
	private Tabpanels 	tabpanels = new Tabpanels();
	
	private Panel mainPanel = new Panel();
	private Borderlayout mainLayout = new Borderlayout();
	
	private Borderlayout mainLayout2 = new Borderlayout();
	private Panel mainPanel2 = new Panel();
	
	private Panel parameterPanel = new Panel();
	private Grid parameterLayout = GridFactory.newGridLayout();
	
	private Panel allocationPanel = new Panel();
	private Grid allocationLayout = GridFactory.newGridLayout();
	private Borderlayout confReceiptLayout = new Borderlayout();
	private WListbox confReceiptTable =  ListboxFactory.newDataTable();
	
	private Panel confReceiptPanel = new Panel();
	private WTableDirEditor productCategorySearch = null;
	private Label productCategoryLabel = new Label();
	
	private WTableDirEditor attributeSetSearch = null;
	private Label attributeSetLabel = new Label();
	
	private WPAttributeDialog attributeSetInstanceSearch = null;
	private Label attributeSetInstanceLabel = new Label();
	private Button bAttSetInstance = new Button();
	private Textbox tAttSetInstance = new Textbox();
	
	private North north = null; 
	private Center center = null;
	private South south = null;
	
	private Button bNext = new Button();
	private Button bCancel = new Button();
	
	private Label lClienteTabRegister = new Label();
	private Label lOrgTabRegister = new Label();
	private Label lProductTabRegister = new Label();
	private Label lIsStocked = new Label(); 
	private Label lProductCategoryTabRegister = new Label();
	private Label lTaxCategory = new Label();
	private Label lUOMTabRegister = new Label();
	private Label lProductTypeTabRegister = new Label();
	private Label lAttributeSetTabRegister = new Label();
	private Label lAttributeSetInstanceTabRegister = new Label();
	private Label lProductSourceTabregister = new Label();
	private Label lUseLifeYearsTabRegister = new Label(); 
	private Label lUseLifeMonthsTabRegister= new Label();
	private Label lLocatorTabRegister = new Label();
	private Label lApprovalType = new Label();
	private Label lIsReturn =  new Label(); 
	private Label lSKUTabRegister =  new Label();  
	private Label lUPCTabRegister = new Label();
	
	private Textbox tClientTabRegister = new Textbox();
	private WTableDirEditor wOrgTabRegister = null;
	private Textbox tProducttabRegister = new Textbox();
	private Checkbox cIsStocked = new Checkbox();
	private Textbox tProductCategoryTabRegister = new Textbox();
	private Textbox tAttributeSetTabRegister = new Textbox();
	private Textbox tAttributeSetInstanceTabRegister = new Textbox();	
	private WTableDirEditor wUOMSearch = null;
	private WTableDirEditor wTaxCategorySearch = null;
	private Listbox listFieldProductType = ListboxFactory.newDropdownListbox();
	private Listbox listFieldProductSource = ListboxFactory.newDropdownListbox();
	private WLocatorEditor wLocator = null;
	private NumberBox useLifeYearsNumberBox =  new NumberBox(false);
	private NumberBox useLifeMonthsNumberBox =  new NumberBox(false);
	private WTableDirEditor wApprovalTypeSearch = null;
	private Checkbox cIsReturn = new Checkbox();
	private Textbox cSKUTabRegister = new Textbox();
	private Textbox cUPCTabRegister = new Textbox();
	
	private Label lMsgSouth = new Label();
	private Button bRegister = new Button();
	private Panel panelSouthTab2 = new Panel();
	private Grid gridPanelSouthTab2 =  GridFactory.newGridLayout();
	private Label lMsgSouthTabRegister = new Label();
	private Button bZoom = new Button();
	
	private static final String aD_Reference_ProdType_ID = MSysConfig.getValue("LCR_REFERENCE_PRODTYPE_ID", 0);
	private static final String aD_Reference_ProdSource_ID = MSysConfig.getValue("LCR_REFERENCE_PRODSOURCE_ID", 0); 
	private static final String c_TaxCategory_ID = MSysConfig.getValue("LCR_C_TAXCATEGORY_ID", 0);
	private static final String c_UOM_ID =  MSysConfig.getValue("LCR_C_UOM_ID", 0);
	private static final String lcr_ApprovalType_ID = MSysConfig.getValue("LCR_APPROVALTYPE_ID", 0);
	
	private Integer m_Product_ID = null; 
	private int t_AttributesetInstance_ID = 0;
	private int m_M_Product_Category_ID = 0;
	private int m_M_AttributeSet_ID = 0;
	private String m_M_AttributeSetInstance_Text = "";
	
	/**
	 *  Dynamic Init
	 *  @throws Exception if Lookups cannot be initialized
	 *  @return true if initialized
	 */
	public boolean dynInit() throws Exception
	{
		log.config("");
		
		lMsgSouth.setText("Selecione uma Categoria de Produto! ");
		
		//  M_Product_Category
		productCategoryLabel.setText(Msg.translate(Env.getCtx(), "M_Product_Category_ID"));
		int m_Product_Category_ID = MColumn.getColumn_ID("M_Product_Category","M_Product_Category_ID");        //  M_Product_Category.M_Product_Category_ID
		String whereClause = " M_Product_Category.IsActive='Y' ";
		MLookupInfo lookupInfo = MLookupFactory.getLookupInfo(Env.getCtx(), m_WindowNo, m_Product_Category_ID, DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "M_Product_Category_ID", 0, false, whereClause);
		MLookup lookup = new MLookup(lookupInfo, 0);
		productCategorySearch = new WTableDirEditor("M_Product_Category_ID", true, false, true, lookup);
		productCategorySearch.addValueChangeListener(this);
				
		//  M_AttributeSetInstance
		bAttSetInstance.setImage("images/PAttribute16.png");
		attributeSetInstanceLabel.setText(Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
		bAttSetInstance.addEventListener(Events.ON_CLICK, this);
		
		// Tab Register Product
		lClienteTabRegister.setText(Msg.translate(Env.getCtx(), "AD_Client_ID"));
		lOrgTabRegister.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
		lProductTabRegister.setText(Msg.translate(Env.getCtx(), "M_Product_ID"));
		lIsStocked.setText(Msg.translate(Env.getCtx(), "IsStocked"));
		lProductCategoryTabRegister.setText(Msg.translate(Env.getCtx(), "M_Product_Category_ID"));
		lTaxCategory.setText(Msg.translate(Env.getCtx(), "C_TaxCategory_ID"));
		lUOMTabRegister.setText(Msg.translate(Env.getCtx(), "C_UOM_ID"));
		lProductTypeTabRegister.setText(Msg.translate(Env.getCtx(), "ProductType"));
		lAttributeSetTabRegister.setText(Msg.translate(Env.getCtx(), "M_AttributeSet_ID"));
		lAttributeSetInstanceTabRegister.setText(Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
		lProductSourceTabregister.setText(Msg.translate(Env.getCtx(), "lbr_ProductSource"));
		lUseLifeYearsTabRegister.setText(Msg.translate(Env.getCtx(), "UseLifeYears"));
		lUseLifeMonthsTabRegister.setText(Msg.translate(Env.getCtx(), "UseLifeMonths"));
		lLocatorTabRegister.setText(Msg.translate(Env.getCtx(), "M_Locator_ID"));
		lApprovalType.setText(Msg.translate(Env.getCtx(), "lcr_ApprovalType_ID"));
		lIsReturn.setText(Msg.translate(Env.getCtx(), "lcr_IsReturn"));
		lSKUTabRegister.setText(Msg.translate(Env.getCtx(), "SKU"));
		lUPCTabRegister.setText(Msg.translate(Env.getCtx(), "UPC"));
		
		// AD_Client
		int ad_Client_ID =  Env.getAD_Client_ID(Env.getCtx());
		MClient client =  new MClient(Env.getCtx(), ad_Client_ID, null);
		tClientTabRegister.setValue(client.getName());
		tClientTabRegister.setReadonly(true);
		
		// AD_ORG
		int l_AD_Org_ID = MColumn.getColumn_ID("AD_Org","AD_Org_ID");
		int ad_Org_ID = Env.getAD_Org_ID(Env.getCtx());
		whereClause = " AD_Org.IsSummary='N' OR AD_Org.AD_Org_ID=0 ";
		lookupInfo = MLookupFactory.getLookupInfo(Env.getCtx(), m_WindowNo, l_AD_Org_ID, DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "AD_Org_ID", 0, false, whereClause);
		lookup = new MLookup(lookupInfo, 0);
		wOrgTabRegister = new WTableDirEditor("AD_Org_ID", true, false, true, lookup);
		wOrgTabRegister.addValueChangeListener(this);
		wOrgTabRegister.setValue(ad_Org_ID);
		
		tProducttabRegister.setReadonly(true);
		tProducttabRegister.setMultiline(true);
		tProducttabRegister.setWidth("10%");
		
		tProductCategoryTabRegister.setReadonly(true);
		tAttributeSetTabRegister.setReadonly(true);
		tAttributeSetInstanceTabRegister.setReadonly(true);
		cIsStocked.setSelected(true);
		cIsReturn.setSelected(false);
		tAttSetInstance.setReadonly(true);
		
		//  C_UOM
		int lc_UOM_ID = MColumn.getColumn_ID("C_UOM","C_UOM_ID");        //  C_UOM.C_UOM_ID
		whereClause = " C_UOM.IsActive='Y' ";
		lookupInfo = MLookupFactory.getLookupInfo(Env.getCtx(), m_WindowNo, lc_UOM_ID, DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "C_UOM_ID", 0, false, whereClause);
		lookup = new MLookup(lookupInfo, 0);
		wUOMSearch = new WTableDirEditor("C_UOM_ID", true, false, true, lookup);
		wUOMSearch.addValueChangeListener(this);
		wUOMSearch.setValue(Integer.parseInt(c_UOM_ID)); // Set default - Unidade
		
		//  C_TaxCategory_ID
		int lc_TaxCategory_ID = MColumn.getColumn_ID("C_TaxCategory","C_TaxCategory_ID");        //  C_TaxCategory.C_TaxCategory_ID
		whereClause = " C_TaxCategory.IsActive='Y' ";
		lookupInfo = MLookupFactory.getLookupInfo(Env.getCtx(), m_WindowNo, lc_TaxCategory_ID, DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "C_TaxCategory_ID", 0, false, whereClause);
		lookup = new MLookup(lookupInfo, 0);
		wTaxCategorySearch = new WTableDirEditor("C_TaxCategory_ID", true, false, true, lookup);
		wTaxCategorySearch.addValueChangeListener(this);
		wTaxCategorySearch.setValue(c_TaxCategory_ID); // Set default - Imposto Brasil
		wTaxCategorySearch.setReadWrite(false);
		
		// ProductType
		ArrayList<KeyNamePair> bProductTypeData = getProductTypeData();
		for(KeyNamePair pp : bProductTypeData)
			listFieldProductType.appendItem(pp.getName(), pp);
		listFieldProductType.setSelectedIndex(0);
		listFieldProductType.addActionListener(this);
		listFieldProductType.setValue("Ítem"); // Set default - Item
		
		// M_Locator_ID
		MLocatorLookup locatorLookup = new MLocatorLookup(Env.getCtx(), m_WindowNo);
		wLocator = new WLocatorEditor("M_Locator_ID", false, false, true, locatorLookup, m_WindowNo);
		
		// ProductSource
		ArrayList<KeyNamePair> bProductSourceData = getProductSourceData();
		for(KeyNamePair pp : bProductSourceData)
			listFieldProductSource.appendItem(pp.getName(), pp);
		listFieldProductSource.setSelectedIndex(0);
		listFieldProductSource.addActionListener(this);
		listFieldProductSource.setValue("0 - Nacional"); // Set default - Nacional
		
		// useLifeYears
		useLifeYearsNumberBox.setValue(0);
		
		// useLifeMonths
		useLifeMonthsNumberBox.setValue(0);
		
		// lcr_ApprovalType_ID
		int t_ApprovalType_ID = MColumn.getColumn_ID("lcr_ApprovalType","lcr_ApprovalType_ID");        //  lcr_ApprovalType.lcr_ApprovalType_ID
		whereClause = " lcr_ApprovalType.IsActive='Y' ";
		lookupInfo = MLookupFactory.getLookupInfo(Env.getCtx(), m_WindowNo, t_ApprovalType_ID, DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "lcr_ApprovalType_ID", 0, false, whereClause);
		lookup = new MLookup(lookupInfo, 0);
		wApprovalTypeSearch = new WTableDirEditor("lcr_ApprovalType_ID", true, false, true, lookup);
		wApprovalTypeSearch.setValue(lcr_ApprovalType_ID); // Set default - Aprovação de Produto
		wApprovalTypeSearch.setReadWrite(false);
		
		return true;
		
	}   //  dynInit
	
	@Override
	protected void initForm() {
		
		try {
			
			tabbox.setWidth("99%");
			tabbox.setHeight("99.30%");
			tabbox.getChildren();
			tabbox.appendChild(tabs);
			tabbox.appendChild(tabpanels);
			tabbox.addEventListener(Events.ON_SELECT, this);
			tabbox.setTabscroll(true);
			
			Tabpanel tabSearchProductPanel = new Tabpanel();
			
			/* Tab1						**/
			Tab tabSearchProduct = new Tab("Buscar Produto");
			tabpanels.appendChild(tabSearchProductPanel);
			tabs.appendChild(tabSearchProduct);
			
			mainPanel.appendChild(mainLayout);
			
			parameterPanel.appendChild(parameterLayout);
			allocationPanel.appendChild(allocationLayout);
			
			// North
			north = new North();
			north.setStyle("border: none");
			north.appendChild(parameterPanel);
			mainLayout.appendChild(north);
	
			Rows rows = null;
			Row row = null;
			
			rows = parameterLayout.newRows();
			row = rows.newRow();
			
			Div div = new Div();
			div.appendChild(productCategoryLabel);
			div.appendChild(new Space());
			div.appendChild(productCategorySearch.getComponent());
			row.appendChild(div);
						
			// Center
			center = new Center();
			confReceiptLayout.appendChild(center);
			confReceiptLayout.setWidth("800px");
			center.appendChild(confReceiptTable); 
			confReceiptTable.setWidth("99%");
			confReceiptTable.setHeight("100%");
			center.setStyle("border: none");
			
			center = new Center();
			center.setFlex(true);
			mainLayout.appendChild(center);
			center.appendChild(confReceiptPanel);
			
			// South
			south = new South();
			south.setStyle("border: none");
			mainLayout.appendChild(south);
			south.appendChild(southPanel);
			southPanel.appendChild(new Separator());
			southPanel.appendChild(allocationPanel);	
			allocationPanel.appendChild(allocationLayout);
			allocationLayout.setWidth("600px");
			
			south = new South();
			south.appendChild(lMsgSouth.rightAlign());
			
			
			rows = allocationLayout.newRows();
			row = rows.newRow();
			Space space = new Space();
			space.setSpacing("200px");
			row.appendChild(space);
			row.appendChild(bNext);
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());	
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());	
			row.appendChild(bCancel);
			bNext.addEventListener(Events.ON_CLICK, this);
			bCancel.addEventListener(Events.ON_CLICK, this);
			bNext.setImage("/images/wfNext24.png");
			bCancel.setImage("/images/Cancel24.png");
			bNext.setLabel("Prosseguir ");
			bNext.setDisabled(true);
			
			confReceiptPanel.appendChild(confReceiptLayout);
			confReceiptPanel.setWidth("100%");
			confReceiptPanel.setHeight("100%");
			confReceiptLayout.setWidth("100%");
			confReceiptLayout.setHeight("100%");
			confReceiptLayout.setStyle("border: none");
		
			south.setStyle("border: none");
			confReceiptLayout.appendChild(south);
			
			tabSearchProductPanel.appendChild(mainLayout);		
			
			/*	Tab2 					**/
			Tabpanel tabRegisterProductPanel = new Tabpanel();
			mainPanel2.appendChild(mainLayout2);
			Tab tabRegisterProduct = new Tab("Cadastrar Produto");
			tabpanels.appendChild(tabRegisterProductPanel);
			tabs.appendChild(tabRegisterProduct);	
			
			mainLayout2.setStyle("height: 80%; width: 99%; position: absolute;");
			
			Panel centerPanel = new Panel();
			Grid centerGrid = new Grid();
			Center center = new Center();
			center.setFlex(true);
			center.appendChild(centerPanel);
			rows = null;
			row = null;
			centerPanel.appendChild(centerGrid);
			centerGrid.setStyle("margin:2; padding:0; position: absolute; align: center; valign: center;");
			rows = centerGrid.newRows();
			
			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			
			row = rows.newRow();
			row.appendChild(lClienteTabRegister.rightAlign());
			row.appendChild(tClientTabRegister);
			row.appendChild(lOrgTabRegister.rightAlign());
			row.appendChild(wOrgTabRegister.getComponent());
			
			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
	
			row = rows.newRow();
			row.appendChild(lProductTabRegister.rightAlign());
			row.appendChild(tProducttabRegister);
			row.appendChild(lIsStocked.rightAlign());
			row.appendChild(cIsStocked);
			tProducttabRegister.setWidth("300px");
			
			row = rows.newRow();
			row.appendChild(lProductCategoryTabRegister.rightAlign());
			row.appendChild(tProductCategoryTabRegister);
			row.appendChild(lTaxCategory.rightAlign());
			row.appendChild(wTaxCategorySearch.getComponent());
			
			row = rows.newRow();
			row.appendChild(lUOMTabRegister.rightAlign());
			row.appendChild(wUOMSearch.getComponent());
			row.appendChild(lProductTypeTabRegister.rightAlign());
			row.appendChild(listFieldProductType);
			
			row = rows.newRow();
			row.appendChild(lAttributeSetTabRegister.rightAlign());
			row.appendChild(tAttributeSetTabRegister);
			row.appendChild(lAttributeSetInstanceTabRegister.rightAlign());
			tAttributeSetInstanceTabRegister.setWidth("300px");
			row.appendChild(tAttributeSetInstanceTabRegister);			
			
			row = rows.newRow();
			row.appendChild(lSKUTabRegister.rightAlign());
			row.appendChild(cSKUTabRegister);
			cSKUTabRegister.setWidth("200px");
			row.appendChild(lUPCTabRegister.rightAlign());
			row.appendChild(cUPCTabRegister);
			
			row = rows.newRow();
			row.appendChild(lIsReturn);
			row.appendChild(cIsReturn);
			row.appendChild(lLocatorTabRegister.rightAlign());
			row.appendChild(wLocator.getComponent());
			wLocator.getComponent().setWidth("75%");
			
			row = rows.newRow();
			row.appendChild(lUseLifeYearsTabRegister.rightAlign());
			row.appendChild(useLifeYearsNumberBox);
			row.appendChild(lUseLifeMonthsTabRegister.rightAlign());
			row.appendChild(useLifeMonthsNumberBox);
			useLifeYearsNumberBox.addEventListener(Events.ON_CHANGE, this);
			
			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			
			row = rows.newRow();
			row.appendChild(lApprovalType.rightAlign());
			row.appendChild(wApprovalTypeSearch.getComponent());
			row.appendChild(new Space());
			row.appendChild(new Space());
			
			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			
			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			
			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			
			row = rows.newRow();
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			
			South south = new South();
			south.setStyle("border: none");
			mainLayout2.appendChild(south);
			south.appendChild(panelSouthTab2);
			panelSouthTab2.appendChild(gridPanelSouthTab2);
			
			rows = gridPanelSouthTab2.newRows();
			row = rows.newRow();
			row.appendChild(new Space());
			
			row = rows.newRow();
			space = new Space();
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			row.appendChild(new Space());
			Hbox hbox = new Hbox();
			space.setSpacing("80%");
			hbox.appendChild(bRegister);
			hbox.appendChild(space);
			hbox.appendChild(bZoom);
			row.appendChild(hbox);
			
			bRegister.addEventListener(Events.ON_CLICK, this);
			bZoom.addEventListener(Events.ON_CLICK, this);
			bRegister.setImage("/images/Ok24.png");
			bZoom.setImage("/images/Zoom24.png");
			bRegister.setLabel("Cadastrar Produto");
			bZoom.setVisible(false);
	
			mainLayout2.appendChild(center);
			tabRegisterProductPanel.appendChild(mainLayout2);	
			
			this.appendChild(tabbox);
			tabbox.addEventListener(Events.ON_SELECT, this);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 *  Load Parameter Attribute Set
	 */
	private void loadAPanelParameter2 ()
	{
		if (confReceiptLayout.getChildren().size() > 2)
			confReceiptLayout.getChildren().remove(2);
		
		Grid parameterLayout2 = GridFactory.newGridLayout();
		Panel parameterPanel2 = new Panel();
		parameterPanel2.appendChild(parameterLayout2);
		parameterLayout2.setWidth("700px");
		
		// North 3
		North north = new North();
		north.setStyle("border: none");
		north.appendChild(parameterPanel2);
		confReceiptLayout.appendChild(north);
		Rows rows = null;
		Row row = null;
		rows = parameterLayout2.newRows();
		row = rows.newRow();
		
		row.appendChild(attributeSetLabel);
		row.appendChild(attributeSetSearch.getComponent());
		
		row.appendChild(attributeSetInstanceLabel);
		row.appendChild(new Space());
		row.appendChild(tAttSetInstance);
		row.appendChild(bAttSetInstance);
		tAttSetInstance.addEventListener(Events.ON_CREATE, this);
		
		row = rows.newRow();
		row.appendChild(new Space());
		
		confReceiptLayout.setWidth("100%");
		confReceiptLayout.setHeight("100%");
		confReceiptLayout.setStyle("border: none");
	}
	
	/**
	 *  Load Products Unable to Register
	 */
	private void loadProd ()
	{		
		/********************************
		 *  Load Products Unable to Register
		 */
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		StringBuffer sql = new StringBuffer(" SELECT mp.M_Product_ID, " +						      // 1 - M_Product_ID
											" mp.Name AS product, " +		 				   	     // 2 - Product
											" mpc.Name AS categoryproduct, " +				  	    // 3 - Categoryproduct
											" cuo.Name AS udm, " +							 	   // 4 - Udm
											" mas.Name AS attributeset, " +						  // 5 - AttributeSet
											" ml.Value AS locator, " +						     // 6 - Locator
											" masi.Description AS attributesetinstance, " +		// 7 - AttributesetInstance
											
											" (SELECT arlt.Name FROM AD_Reference ar  JOIN AD_Ref_List arl		ON (arl.AD_Reference_ID = ar.AD_Reference_ID)" +
											" JOIN AD_Ref_List_Trl arlt	ON (arlt.AD_Ref_List_ID = arl.AD_Ref_List_ID) Where arlt.AD_Language='pt_BR'" +
											" AND  ar.AD_Reference_ID=270 AND arl.IsActive='Y' AND arl.Value = mp.ProductType) AS producttype " +
																						    // 8 - Producttype 
											" FROM  M_Product mp" +
											" LEFT JOIN M_Product_Category mpc		ON (mpc.M_Product_Category_ID = mp.M_Product_Category_ID)" +
											" LEFT JOIN C_UOM cuo 					ON (cuo.C_UOM_ID = mp.C_UOM_ID)" +
											" LEFT JOIN M_AttributeSet mas			ON (mas.M_AttributeSet_ID = mp.M_AttributeSet_ID)" +
											" LEFT JOIN M_Locator ml 				ON (ml.M_Locator_ID = mp.M_Locator_ID)" +
											" LEFT JOIN M_AttributeSetInstance masi	ON (masi.M_AttributeSetInstance_ID = mp.M_AttributeSetInstance_ID)" +
											
											" WHERE mp.IsActive='Y'" +
											" AND (mp.DocStatus='AP' OR mp.DocStatus IS NULL) ");
		 
		if (m_M_Product_Category_ID != 0)                           
			sql.append(" AND mp.M_Product_Category_ID=? ")
			;
		if (m_M_AttributeSet_ID != 0)                           
			sql.append(" AND mp.M_AttributeSet_ID=? ")
			;
		
		if(m_M_AttributeSetInstance_Text.length() != 0)
			sql.append(breakText());
		
		sql.append(" ORDER BY mp.Name, mpc.Name ");
											
		int y = 1;
		
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql.toString(), null);
			
			if (m_M_Product_Category_ID != 0)  
				pstmt.setInt(y++, m_M_Product_Category_ID);
			
			if (m_M_AttributeSet_ID != 0)   
				pstmt.setInt(y++, m_M_AttributeSet_ID);
				
			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				Vector<Object> line = new Vector<Object>();
			
				KeyNamePair pp = new KeyNamePair(rs.getInt(1), rs.getString(2));
				line.add(pp); 			   	  // 1 - M_Product_ID  // 2-Product     			
				line.add(rs.getString(3)); 	  // 3 - Categoryproduct
				line.add(rs.getString(4)); 	  // 4 - Udm
				line.add(rs.getString(5)); 	  // 5 - AttributeSet
				line.add(rs.getString(6)); 	  // 6 - Locator
				line.add(rs.getString(7));    // 7 - AttributesetInstance
				line.add(rs.getString(8));    // 8 - Producttype        				
				
				data.add(line);
				
				if (rs.getRow() >= 200 )
					break;
				
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
		}
		confReceiptTable.clear();
		//  Remove previous listeners
		confReceiptTable.getModel().removeTableModelListener(this);
		//  Header Info
		Vector<String> columnNames = new Vector<String>();
		columnNames.add(Msg.translate(Env.getCtx(), "M_Product_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "M_Product_Category_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "C_UOM_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "M_AttributeSet_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "M_Locator_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
		columnNames.add(Msg.translate(Env.getCtx(), "ProductType"));
		
		
		//  Set Model
		ListModelTable modelP = new ListModelTable(data);
		modelP.addTableModelListener(this);
		confReceiptTable.setData(modelP, columnNames);
		//

		int i = 0;
		
		confReceiptTable.setColumnClass(i++, String.class, true);    //  1-Produto
		confReceiptTable.setColumnClass(i++, String.class, true);    //  2-Categoria do Produto
		confReceiptTable.setColumnClass(i++, String.class, true); 	 //  3-UDM
		confReceiptTable.setColumnClass(i++, String.class, true); 	 //  4-Conjunto de Atributos
		confReceiptTable.setColumnClass(i++, String.class, true);    //  5-Localizador
		confReceiptTable.setColumnClass(i++, String.class, true);    //  6-Instância do Conjunto de Atributos
		confReceiptTable.setColumnClass(i++, String.class, true);    //  7-Tipo de Produto
		
		//  Table UI
		confReceiptTable.autoSize();

	}   //  loadProd

	@Override
	public void valueChange(ValueChangeEvent evt) {
		String name = evt.getPropertyName();
		Object value = evt.getNewValue();
		log.config(name + "=" + value);
		
		if (name.equals("M_Product_Category_ID"))
		{
			productCategorySearch.setValue(value);
			tAttSetInstance.setText("");
			m_M_AttributeSetInstance_Text = "";
			m_M_Product_Category_ID = 0;
			m_M_AttributeSet_ID = 0;
			
			if (value != null){
				
				m_M_Product_Category_ID = ((Integer)value).intValue();
			
				//  M_AttributeSet
				attributeSetLabel.setText("Conjunto de Atributos");
				int m_AttributeSet_ID = MColumn.getColumn_ID("M_AttributeSet","M_AttributeSet_ID");        //  M_AttributeSet.M_AttributeSet_ID
				String whereClause = "M_AttributeSet.M_Product_Category_ID= " + m_M_Product_Category_ID +" AND M_AttributeSet.IsApproved='Y' ";
				MLookupInfo lookupOrderInfo = MLookupFactory.getLookupInfo(Env.getCtx(), m_WindowNo, m_AttributeSet_ID, DisplayType.TableDir, Env.getLanguage(Env.getCtx()), "M_AttributeSet_ID", 0, false, whereClause);
				MLookup lookupOrder = new MLookup(lookupOrderInfo, 0);
				attributeSetSearch = new WTableDirEditor("M_AttributeSet_ID", false, false , true, lookupOrder);
				attributeSetSearch.addValueChangeListener(this);
				bNext.setEnabled(false);
				bRegister.setEnabled(true);
				bAttSetInstance.setEnabled(true);
			}
			
			loadAPanelParameter2();
			loadProd();
		} 
		
		else if (name.equals("M_AttributeSet_ID"))
		{
			attributeSetSearch.setValue(value);
			m_M_AttributeSet_ID = 0;
			
			if (value != null)
				m_M_AttributeSet_ID = ((Integer)value).intValue();
			tAttSetInstance.setText("");
			m_M_AttributeSetInstance_Text = "";
			bNext.setEnabled(false);
			bRegister.setEnabled(true);
			bAttSetInstance.setEnabled(true);
			
			loadProd();
		} 
		
		calculate();
		
	}

	@Override
	public void tableChanged(WTableModelEvent event) {
		// TODO Auto-generated method stub
		 
	}

	@Override
	public void onEvent(Event event) throws Exception {
		

		if (event.getTarget().equals(bAttSetInstance))
		{
			m_M_AttributeSetInstance_Text = "";
			int m_AttributeSetInstance_ID = MColumn.getColumn_ID("M_AttributeSetInstance","M_AttributeSetInstance_ID"); 
			MAttributeSetInstance attSetInstace = new MAttributeSetInstance(Env.getCtx(), 0, m_M_AttributeSet_ID, null);
			attributeSetInstanceSearch = new WPAttributeDialog(attSetInstace.get_ID(), m_M_AttributeSet_ID, 0, 0, true, 
					m_AttributeSetInstance_ID, SessionManager.getAppDesktop().registerWindow(this));
			tAttSetInstance.setText("");
			t_AttributesetInstance_ID = 0;
			
			if (attributeSetInstanceSearch.isChanged()){
				t_AttributesetInstance_ID = DB.getSQLValue(null, "SELECT MAX(M_AttributeSetInstance_ID) FROM M_AttributeSetInstance");
				attSetInstace = new MAttributeSetInstance(Env.getCtx(), t_AttributesetInstance_ID, m_M_AttributeSet_ID, null);
				String str = attSetInstace.getDescription();
				str = str.replaceAll("_NAO INFORMADO", "*");
				str = str.replaceAll("_N�O INFORMADO", "*");
				tAttSetInstance.setText(str);
				m_M_AttributeSetInstance_Text = tAttSetInstance.getValue();
				
				loadProd();
				
				int rowsCount = confReceiptTable.getRowCount();
				
				if (m_M_AttributeSetInstance_Text.length()>0 && rowsCount==0){
					bNext.setEnabled(true);	
					bRegister.setEnabled(true);
					bAttSetInstance.setEnabled(false);
				}else
					bAttSetInstance.setEnabled(true);
			}
			
		}
		
		
		if (event.getTarget().equals(bNext))
		{
			
			tabbox.setSelectedIndex(1);
			
			loadFields();
		}
		
		if (event.getTarget().equals(bCancel))
			dispose();
		
		
		if (event.getTarget().equals(bRegister))
		{
			if (!validations())
				return;
			
			int opt =  Messagebox.showDialog("Deseja cadastrar o Produto?",
					"Cadastrar Produto", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			
			if (opt==1){
				if (registerProduct()){
					clean();
					tabbox.setSelectedIndex(0);
					lMsgSouth.setText("<<Produto Cadastrado com Sucesso!>> Faça uma nova busca!");
					AEnv.zoom(MProduct.Table_ID, m_Product_ID);	
				}
			}
		}
		
		if (event.getTarget().equals(bZoom))
		{
			AEnv.zoom(MProduct.Table_ID, m_Product_ID);		
		}
		
		
		if (event.getTarget() instanceof Tab)
		{
			int rows = confReceiptTable.getRowCount();
			
			if(tabbox.getSelectedIndex() == 1 
					&& tAttSetInstance.getValue().length()==0){
				FDialog.warn(m_WindowNo, "Não foi informado nenhuma Instância do conjunto de Atributos!", getTitle());
				tabbox.setSelectedIndex(0);
			}
			
			if(tabbox.getSelectedIndex() == 1 
					&& tAttSetInstance.getValue().length()>0 && rows>0){
				FDialog.warn(m_WindowNo, "Foram encontrados produtos com o mesmo nome!", getTitle());
				tabbox.setSelectedIndex(0);
			}	
			
			if(tabbox.getSelectedIndex() == 1 && tAttSetInstance.getValue().length()>0 && rows==0){
				loadFields();
			}
		}
		
		if (event.getTarget() instanceof Decimalbox){
			useLifeMonthsNumberBox.setValue(useLifeYearsNumberBox.getValue().multiply(new BigDecimal(12)));
		}
		
		calculate();
	}

	/**
	 * Called by org.adempiere.webui.panel.ADForm.openForm()
	 * @return CustomForm
	 */ 
	public ADForm getForm() {
		return form;
	}
	
	
	/**
	 * 	Get ProductType
	 * 	@returns ArrayList<KeyNamePair>
	 */
	public ArrayList<KeyNamePair> getProductTypeData()
	{
		ArrayList<KeyNamePair> data = new ArrayList<KeyNamePair>();
		
		//  Optional BusinessPartner with unpaid AP Invoices
		KeyNamePair pp = new KeyNamePair(0, "");
		data.add(pp);
		
		String sql = " Select adrl.AD_Ref_List_ID, adrlt.Name" +
					 " From AD_Reference adr " +
					 " Join AD_Ref_List  adrl 	   ON (adr.AD_Reference_ID = adrl.AD_Reference_ID)" +
					 " Join AD_Ref_List_Trl adrlt ON (adrlt.AD_Ref_List_ID = adrl.AD_Ref_List_ID)" +
					 " Where adr.AD_Reference_ID = " + aD_Reference_ProdType_ID +
					 " And adrl.IsActive='Y'" +
					 " And adrlt.AD_Language='pt_BR'" +
					 " ORDER BY 2";

		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				pp = new KeyNamePair(rs.getInt(1), rs.getString(2));
				data.add(pp);
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		
		return data;
	}	//	getProductTypeData
	
	/**
	 * 	Get ProductSource
	 * 	@return ArrayList<KeyNamePair>
	 */
	public ArrayList<KeyNamePair> getProductSourceData()
	{
		ArrayList<KeyNamePair> data = new ArrayList<KeyNamePair>();
		
		//  Optional BusinessPartner with unpaid AP Invoices
		KeyNamePair pp = new KeyNamePair(0, "");
		data.add(pp);
		
		String sql = " Select adrl.AD_Ref_List_ID, adrlt.Name" +
					 " From AD_Reference adr " +
					 " Join AD_Ref_List  adrl 	   		ON (adr.AD_Reference_ID = adrl.AD_Reference_ID)" +
					 " Join AD_Ref_List_Trl adrlt 		ON (adrlt.AD_Ref_List_ID = adrl.AD_Ref_List_ID)" +
					 " Where adr.AD_Reference_ID = " + aD_Reference_ProdSource_ID +
					 " And adrl.IsActive='Y'" +
					 " And adrlt.AD_Language='pt_BR'" +
					 " ORDER BY 2";

		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql, null);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next())
			{
				pp = new KeyNamePair(rs.getInt(1), rs.getString(2));
				data.add(pp);
			}
			
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		
		return data;
		
	}	//	getProductSourceData
	
	
	/**
	 * Set the whereclause by  Attribute instance set
	 * @return String where
	 */
	public String breakText(){
		
		 String[] quebra = m_M_AttributeSetInstance_Text.split("_");
		 
		 StringBuffer where =  new StringBuffer();
		 
		 if (quebra.length==0)
			 return "";
		 
		 for (int i=0; i < quebra.length; i++){
		                          
			 where.append(" AND mp.Name ilike '%" + quebra[i] + "%'")
			 ;
		 }
		 
		return where.toString();
	}
	
	/**
	 * Register the product
	 */
	public boolean registerProduct(){
		
		Trx trx = Trx.get(Trx.createTrxName("FCPR"), true);
		
		boolean isRegister = false;
		
		Integer ad_Org_ID =  (Integer) wOrgTabRegister.getValue();
		int m_Product_Category_ID = DB.getSQLValue(null, "SELECT M_Product_Category_ID FROM M_Product_Category WHERE Name ilike '" + tProductCategoryTabRegister.getValue() + "' "); 
		boolean IsStocked =  cIsStocked.isSelected();
		boolean isReturn =  cIsReturn.isSelected(); 
		int c_Uom_ID = (Integer)wUOMSearch.getValue();
		ListItem listItem = listFieldProductType.getItemAtIndex(listFieldProductType.getSelectedIndex());
		
		String sql = " SELECT arl.Value " +
					 " FROM AD_Ref_List arl" +
					 " JOIN AD_Reference adr 		ON (arl.AD_Reference_ID = adr.AD_Reference_ID)" +
					 " JOIN AD_Ref_List_Trl arlt 	ON (arlt.AD_Ref_List_ID = arl.AD_Ref_List_ID AND  arlt.AD_Language='pt_BR')" +
					 " WHERE adr.AD_Reference_ID=" + aD_Reference_ProdType_ID +
					 " AND arl.ISActive='Y'" +
					 " AND arlt.Name ilike '" + listItem.getValue() + "' ";
		
		String productType = DB.getSQLValueString(null, sql);
		
		int m_Locator_Id = 0;
		if (wLocator.getValue() != null)
			m_Locator_Id = (Integer)wLocator.getValue();
		
		int m_AttributeSet_ID = DB.getSQLValue(null, "SELECT M_AttributeSet_ID FROM M_AttributeSet WHERE Name ilike '" + tAttributeSetTabRegister.getValue() + "' " ); 
		
		String sql3 = " SELECT M_AttributeSetInstance_ID " +
					  " FROM M_AttributeSetInstance" +
					  " WHERE Description ilike replace('"+tAttributeSetInstanceTabRegister.getValue()+"','*','_NAO INFORMADO')";
		
		int m_AttributeSetInstance_ID = DB.getSQLValue(trx.getTrxName(),sql3);
		
		String sql2 = " SELECT adrl.Value" +
				 	  " FROM AD_Reference adr " +
				 	  " JOIN AD_Ref_List  adrl 	   		ON (adr.AD_Reference_ID = adrl.AD_Reference_ID)" +
				 	  " JOIN AD_Ref_List_Trl adrlt 		ON (adrlt.AD_Ref_List_ID = adrl.AD_Ref_List_ID)" +
				 	  " WHERE adr.AD_Reference_ID = " + aD_Reference_ProdSource_ID +
				 	  " AND adrl.IsActive='Y'" +
				 	  " AND adrlt.AD_Language='pt_BR' " +
				 	  " AND adrlt.Name ilike '" + listItem.getValue() + "' ";
	
		int useLifeYears = 0;
		if (useLifeYearsNumberBox.getValue() != null)
			useLifeYears = useLifeYearsNumberBox.getValue().intValue(); 
		
		int userLifeMonths = 0;
		if (useLifeMonthsNumberBox.getValue() != null)
			userLifeMonths = useLifeMonthsNumberBox.getValue().intValue();
		
		MProduct newProduct = new MProduct(Env.getCtx(), 0, trx.getTrxName());
		newProduct.setAD_Org_ID(ad_Org_ID);
		newProduct.setValue(tProducttabRegister.getValue());
		newProduct.setName(tProducttabRegister.getValue());
		newProduct.setM_Product_Category_ID(m_Product_Category_ID);
		newProduct.setIsStocked(IsStocked);
		newProduct.setlcr_IsReturn(isReturn);
		newProduct.setC_TaxCategory_ID(Integer.parseInt(c_TaxCategory_ID));
		newProduct.setC_UOM_ID(c_Uom_ID);
		newProduct.setProductType(productType);
		newProduct.setIsSubmitApproval(false);
		newProduct.setSKU(cSKUTabRegister.getValue());
		if (cUPCTabRegister.getValue().length()>0)
			newProduct.setUPC(cUPCTabRegister.getValue());
		
		if (m_Locator_Id>0)
			newProduct.setM_Locator_ID(m_Locator_Id);
		
		newProduct.setM_AttributeSet_ID(m_AttributeSet_ID);
		newProduct.setlbr_ProductSource("0");
		
		if (useLifeYears>0)
			newProduct.setUseLifeYears(useLifeYears);
		
		if (userLifeMonths>0)
			newProduct.setUseLifeMonths(userLifeMonths);
		
		newProduct.setlcr_ApprovalType_ID(Integer.parseInt(lcr_ApprovalType_ID));
		
		String sql4 = ""; 
		
		if (newProduct.save()){
			bRegister.setDisabled(true);
			lMsgSouthTabRegister.setText("Produto - " + newProduct.getName() + " cadastrado.");
			bAttSetInstance.setEnabled(true);
			m_Product_ID = newProduct.get_ID();
			isRegister=true;
			
			try{
				trx.commit();
				sql4 = "Update M_Product Set M_AttributeSetInstance_ID =" + m_AttributeSetInstance_ID +" Where M_Product_ID = "+ newProduct.get_ID();
				DB.executeUpdate(sql4, trx.getTrxName());
			}catch(Exception e){
				FDialog.error(m_WindowNo, this, "Error",e.toString());
				trx.rollback();
			}
			
		}else{
			lMsgSouthTabRegister.setText("O Produto não foi cadastrado!");
			trx.rollback();
		}	
		
		trx.close();
		
		return isRegister;
	}
	
	/**
	 * 	Dispose
	 */
	public void dispose()
	{
		SessionManager.getAppDesktop().closeActiveWindow();
	}	//	dispose
	
	
	/**
	 *  Calculate Allocation info
	 */
	private void calculate()
	{
		//
		int rows = confReceiptTable.getRowCount();
		if (rows>0)
			lMsgSouth.setText("Qtde: "+rows);
		else if (tAttSetInstance.getValue().length()>0 && rows==0)
			lMsgSouth.setText("Pressione o Botão <<Prosseguir>> para efetuar o cadastro do Produto!");
		
	}   //  calculate
	
	/**
	 * Clean Window tab Register
	 * @author Icaro Caetano
	 */
	public void clean(){
		tClientTabRegister.setValue(null);
		wOrgTabRegister.setValue(null);
		tProducttabRegister.setValue(null);
		cIsStocked.setSelected(false);
		tProductCategoryTabRegister.setValue(null);
		wTaxCategorySearch.setValue(null);
		wUOMSearch.setValue(null);
		tAttributeSetTabRegister.setValue(null);
		tAttributeSetInstanceTabRegister.setValue(null);
		useLifeYearsNumberBox.setValue(0);
		useLifeMonthsNumberBox.setValue(0);
		wApprovalTypeSearch.setValue(null);
		tAttSetInstance.setValue("");
	}
	
	/**
	 * Load Fields
	 */
	public void loadFields(){
		
		int ad_Client_ID =  Env.getAD_Client_ID(Env.getCtx());
		MProductCategory category = new MProductCategory(Env.getCtx(), m_M_Product_Category_ID, null);
		MClient client =  new MClient(Env.getCtx(), ad_Client_ID, null);
		MAttributeSet attributeSet = new MAttributeSet(Env.getCtx(), m_M_AttributeSet_ID, null);
		int ad_Org_ID = Env.getAD_Org_ID(Env.getCtx());
		
		tClientTabRegister.setValue(client.getName());
		wOrgTabRegister.setValue(ad_Org_ID);
		wLocator.setValue(null);
		cSKUTabRegister.setValue(m_M_Product_Category_ID + "." + m_M_AttributeSet_ID 
				+ "." + t_AttributesetInstance_ID);
		wUOMSearch.setValue(Integer.parseInt(c_UOM_ID));
		wTaxCategorySearch.setValue(c_TaxCategory_ID);
		useLifeYearsNumberBox.setValue(0);
		useLifeMonthsNumberBox.setValue(0);
		tProducttabRegister.setValue(m_M_AttributeSetInstance_Text);
		tProductCategoryTabRegister.setValue(category.getName());
		tAttributeSetTabRegister.setValue(attributeSet.getName());
		tAttributeSetInstanceTabRegister.setValue(m_M_AttributeSetInstance_Text);
		wApprovalTypeSearch.setValue(lcr_ApprovalType_ID);
	}
	
	/**
	 * Validations register of the product
	 * 
	 * @return Boolean true/false
	 * @throws InterruptedException
	 */
	public Boolean validations() throws InterruptedException{
		
		if (wOrgTabRegister.getValue()==null){
			Messagebox.showDialog("É Obrigatório informar a Organização.", 
					"Cadastrar Produto", Messagebox.OK, Messagebox.INFORMATION);
			return false;
		}
		
		if (wLocator.getValue() == null || wLocator.getValue().toString().length()==0){
			Messagebox.showDialog("É Obrigatório informar um localizador.", 
					"Cadastrar Produto", Messagebox.OK, Messagebox.INFORMATION);
			return false;
		}
		
		if (useLifeYearsNumberBox.getValue().intValue() == 0 
				&& useLifeMonthsNumberBox.getValue().intValue()==0){
			Messagebox.showDialog("É necessário informar a vida útil econômica do Produto.", 
					"Cadastrar Produto", Messagebox.OK, Messagebox.INFORMATION);
			return false;
		}
		
		int countUPC =0;
		
		if (cUPCTabRegister.getValue().length()>0){
			
			String sql = "SELECT COUNT(*) FROM M_Product WHERE  UPC ILIKE '"
					+ cUPCTabRegister.getValue().trim() + "' " + " AND IsActive='Y'";

			countUPC = DB.getSQLValue(null, sql);
			
			if (countUPC > 0){
				Messagebox.showDialog("Esse Código de Barras já está cadastrado num Produto.", 
						"Cadastrar Produto", Messagebox.OK, Messagebox.INFORMATION);
				return false;
			}
			
			String sql2 = "SELECT COUNT(*) FROM M_Product WHERE  SKU ILIKE '"
					+ cUPCTabRegister.getValue().trim() + "' " + " AND IsActive='Y'";

			countUPC = DB.getSQLValue(null, sql2);

			if (countUPC > 0){
				Messagebox.showDialog("Esse Código de Barras já está cadastrado num Produto no campo UCE.", 
						"Cadastrar Produto", Messagebox.OK, Messagebox.INFORMATION);
				return false;
			}
		}
		
		return true;
	}
	
}