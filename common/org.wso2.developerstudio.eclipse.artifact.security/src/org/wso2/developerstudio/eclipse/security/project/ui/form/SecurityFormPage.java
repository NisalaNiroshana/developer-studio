/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.developerstudio.eclipse.security.project.ui.form;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.developerstudio.eclipse.artifact.security.utils.SecurityFormMessageConstants;
import org.wso2.developerstudio.eclipse.logging.core.IDeveloperStudioLog;
import org.wso2.developerstudio.eclipse.logging.core.Logger;
import org.wso2.developerstudio.eclipse.platform.core.utils.SWTResourceManager;
import org.wso2.developerstudio.eclipse.security.Activator;
import org.wso2.developerstudio.eclipse.security.project.model.Policy2;
import org.wso2.developerstudio.eclipse.security.project.utils.SecurityPolicies;
import org.wso2.developerstudio.eclipse.security.project.utils.SecurityPolicyUtils;
import org.wso2.developerstudio.eclipse.security.project.utils.SecurityTemplateUtil;
import org.xml.sax.SAXException;

public class SecurityFormPage extends FormPage {

	private static IDeveloperStudioLog log = Logger.getLog(Activator.PLUGIN_ID);
	boolean isSave = false;

	private static final String ORG_WSO2_CARBON_SECURITY_CRYPTO_ALIAS = "org.wso2.carbon.security.crypto.alias";
	private static final String ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES = "org.wso2.carbon.security.crypto.truststores";
	private static final String ORG_WSO2_STRATOS_TENANT_ID = "org.wso2.stratos.tenant.id";
	private static final String ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE = "org.wso2.carbon.security.crypto.privatestore";

	// Constants for Rampart Config
	private static final String KERBEROSSIGNANDENCRYPT = "kerberossignandencrypt";
	private static final String RAMPART_CONFIG_USER = "rampart.config.user";
	private static final String RAMPART_CONFIG = "rampart:RampartConfig";
	private static final String RAMPART_USER = "rampart:user";
	private static final String RAMPART_ENCRYPTION_USER = "rampart:encryptionUser";
	private static final String RAMPART_NONCE_LIFE_TIME = "rampart:nonceLifeTime";
	private static final String RAMPART_TOKEN_STORE_CLASS = "rampart:tokenStoreClass";
	private static final String RAMPART_TIMESTAMP_STRICT = "rampart:timestampStrict";
	private static final String RAMPART_TIMESTAMP_MAX_SKEW = "rampart:timestampMaxSkew";
	private static final String RAMPART_TIMESTAMP_TTL = "rampart:timestampTTL";
	private static final String RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS = "rampart:timestampPrecisionInMilliseconds";
	private static final String RAMPART_ENCRYPTION_CRYPTO = "rampart:encryptionCrypto";
	private static final String RAMPART_SIGNATURE_CRYPTO = "rampart:signatureCrypto";
	private static final String RAMPART_KERBEROS_CONFIG = "rampart:kerberosConfig";
	private static final String RAMPART_CRYPTO = "rampart:crypto";
	private static final String RAMPART_PROPERTY = "rampart:property";
	private static final String RAMPART_PROPERTY_NAME = "name";
	private static final String USER_ROLE = "User Roles";

	// Rampart default values
	private static final String RAMPART_ENCRYPTION_USER_VALUE = "useReqSigCert";
	private static final String RAMPART_TOKEN_STORE_CLASS_VALUE = "org.wso2.carbon.security.util.SecurityTokenStore";
	private static final String RAMPART_TIME_VALUE = "300";
	private static final String RAMPART_TENANT_VALUE = "-1234";

	// Private Store Constants
	private static final String WSO2_PRIVATESTORE = "wso2carbon.jks";
	private static final String WSO2_PRIVATESTORE_ALIAS = "wso2carbon";

	// Category Names
	private static final String BASIC_SCENARIOS = "Basic Scenarios";
	private static final String ADVANCED_SCENARIOS = "Advanced Scenarios";
	private static final String ADVANCE_CONFIGURATION = "Advance Configuration(Rampart)";

	// Label Names
	private static final String LABEL_USER = "User :";
	private static final String LABEL_ENCRYPTION_USER = "encryptionUser :";
	private static final String LABEL_PRECISION = "PrecisionInMilliseconds :";
	private static final String LABEL_TIMESTAMP_TTL = "timestampTTL :";
	private static final String LABEL_TIMESTAMP_MAX = "timestampMaxSkew :";
	private static final String LABEL_TIMESTAMP_STRICT = "timestampStrict :";
	private static final String LABEL_TOKEN_STORE_CLASS = "tokenStoreClass :";
	private static final String LABEL_NONCELIFETIME = "nonceLifeTime :";
	private static final String LABEL_PRIVATE_STORE = "Privatestore :";
	private static final String EDITOR_TITLE = "WS-Policy for Service";
	private static final String VALUE_TRUE = "true";
	private static final String VALUE_FALSE = "false";
	private static final String FILE_PREFIX = "scenario";
	private static final String FILE_POSTFIX = "-policy.xml";
	private static final String SANS = "Sans";

	// Section Names
	private static final String SECTION_SERVICE_INFO = "Service Info";
	private static final String SECTION_SECURITY_SERVICE = "Security for the service";
	private static final String SECTION_RAMPART_CONFIGURATION = "Rampart Configuration";
	private static final String SECTION_ENCRYPTION_PROPERTIES = "Encryption Properties";
	private static final String SECTION_SIGNATURE_PROPOERTIES = "Signature Properties";

	// Rampart Configs
	private static final String ALIAS = ":Alias";
	private static final String PRIVATESTORE = ":Privatestore";
	private static final String TRUSTSTORES = ":Truststores";
	private static final String TENANT_ID = ":Tenant id";
	private static final String USER = ":User";
	private static final String EN = "en";
	private static final String SIGN = "sign";
	private static final String POLICIES = "policies/";

	private static final String POLICY_OBJECT_UT = "UTOverTransport";
	private static final String POLICY_UT = "UsernameToken";
	private static final String POLICY_KERBEROS = "Kerberos Authentication - Sign - Sign based on a Kerberos Token";
	// Messages
	private static final String TIP_MESSAGE = "Description not available";

	public IProject project;
	private Policy2 policyObject;
	private InputStream policyTemplateStream;
	File policyTemplateFile;
	private File inputFile;
	private String policyFileName;
	private String selectedPolicy;
	private Document doc;
	private Element rampart;
	private String policyID;
	private Display display;

	private FormToolkit toolkit;
	private ScrolledForm form;
	private Composite body;
	private Object[] resultService;
	private Object[] enresult;
	private Object[] signresult;

	private static Map<String, String> rampartDataMap;
	private static Map<String, String> encryptDataMap;
	private static Map<String, String> signDataMap;

	private Map<String, Text> encryptControlMap;
	private Map<String, Text> signControlMap;
	private Map<String, Object> rampartControlMap;

	private Map<String, Button> policyeMap;

	SecurityFormEditor formEditor;

	// UI components
	private Button policyOneUserRolesButton;
	private Button policySevenUserRolesButton;
	private Button policyEightUserRolesButton;
	private Button policyFourteenUserRolesButton;
	private Button policyFifteenUserRolesButton;
	private Text txtPrivateStore;
	private Text txtRampartTimestampMaxSkew;
	private Text txtRampartUser;
	private Text txtRampartEncryptionUser;
	private Text txtRampartMinTTL;
	private Text txtRampartTokenStoreClass;
	private Text txtRampartNonceLifeTime;
	private Combo cmbRampartTimestampStrict;
	private Combo cmbRampartTimestampPrecision;

	public SecurityFormPage(FormEditor editor, String id, String title, IProject iproject, File file, Display display) {
		super(editor, id, title);

		rampartDataMap = new HashMap<>();
		encryptDataMap = new HashMap<>();
		signDataMap = new HashMap<>();

		// Fill Data Maps with default values
		rampartDataMap.put(RAMPART_USER, WSO2_PRIVATESTORE_ALIAS);
		rampartDataMap.put(RAMPART_ENCRYPTION_USER, RAMPART_ENCRYPTION_USER_VALUE);
		rampartDataMap.put(RAMPART_TIMESTAMP_TTL, RAMPART_TIME_VALUE);
		rampartDataMap.put(RAMPART_TIMESTAMP_MAX_SKEW, RAMPART_TIME_VALUE);
		rampartDataMap.put(RAMPART_TOKEN_STORE_CLASS, RAMPART_TOKEN_STORE_CLASS_VALUE);
		rampartDataMap.put(RAMPART_NONCE_LIFE_TIME, RAMPART_TIME_VALUE);
		rampartDataMap.put(RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS, VALUE_FALSE);
		rampartDataMap.put(RAMPART_TIMESTAMP_STRICT, VALUE_FALSE);

		encryptDataMap.put(ORG_WSO2_CARBON_SECURITY_CRYPTO_ALIAS, WSO2_PRIVATESTORE_ALIAS);
		encryptDataMap.put(ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE, WSO2_PRIVATESTORE);
		encryptDataMap.put(ORG_WSO2_STRATOS_TENANT_ID, RAMPART_TENANT_VALUE);
		encryptDataMap.put(ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES, WSO2_PRIVATESTORE);
		encryptDataMap.put(RAMPART_CONFIG_USER, WSO2_PRIVATESTORE_ALIAS);

		signDataMap.put(ORG_WSO2_CARBON_SECURITY_CRYPTO_ALIAS, WSO2_PRIVATESTORE_ALIAS);
		signDataMap.put(ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE, WSO2_PRIVATESTORE);
		signDataMap.put(ORG_WSO2_STRATOS_TENANT_ID, RAMPART_TENANT_VALUE);
		signDataMap.put(ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES, WSO2_PRIVATESTORE);
		signDataMap.put(RAMPART_CONFIG_USER, WSO2_PRIVATESTORE_ALIAS);

		encryptControlMap = new HashMap<>();
		signControlMap = new HashMap<>();
		rampartControlMap = new HashMap<>();

		policyeMap = new HashMap<>();
		inputFile = file;
		project = iproject;
		formEditor = (SecurityFormEditor) editor;
		this.display = display;
	}

	protected void createFormContent(IManagedForm managedForm) {

		toolkit = managedForm.getToolkit();
		form = managedForm.getForm();
		form.setText(EDITOR_TITLE);
		body = form.getBody();
		GridLayout gridParentLayout = new GridLayout(1, true);

		body.setLayout(gridParentLayout);
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);

		Object[] result = CreateMainSection(toolkit, body, SECTION_SECURITY_SERVICE, 10, 70, 600, 30, true);
		Composite seccomposite = (Composite) result[1];
		GridLayout gridSecLayout = new GridLayout(5, false);
		seccomposite.setLayout(gridSecLayout);

		createCategory(toolkit, seccomposite, BASIC_SCENARIOS);

		try {
			createSecurityScenarioOptionButtons(seccomposite, SecurityPolicyUtils.getInstance()
					.getBasicSecurityScenarios(), managedForm, 0, body);
		} catch (IOException | JAXBException e) {
			log.error(SecurityFormMessageConstants.MESSAGE_READ_POLICY, e);
			MessageBox msg = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			msg.setMessage(SecurityFormMessageConstants.MESSAGE_POLICYFILE_READ_ERROR);
			msg.open();
		}

		resultService = CreateSecuritySection(toolkit, body, SECTION_SERVICE_INFO, 10, 70, 600, 30, true);
		Composite serviceInfoMainComposite = (Composite) resultService[1];
		GridLayout gridserviceLayout = new GridLayout();
		serviceInfoMainComposite.setLayout(gridserviceLayout);

		Composite compositeBasicInfo = new Composite(serviceInfoMainComposite, SWT.NULL);
		GridLayout BasicInfoLayout = new GridLayout(3, false);
		compositeBasicInfo.setLayout(BasicInfoLayout);

		toolkit.createLabel(compositeBasicInfo, LABEL_PRIVATE_STORE);

		txtPrivateStore = new Text(compositeBasicInfo, SWT.FLAT);
		txtPrivateStore.setBounds(new org.eclipse.swt.graphics.Rectangle(92, 40, 84, 28));
		txtPrivateStore.setText(WSO2_PRIVATESTORE);
		GridData keyslayoutData = new GridData();
		keyslayoutData.minimumWidth = 200;
		keyslayoutData.horizontalAlignment = SWT.FILL;
		keyslayoutData.grabExcessHorizontalSpace = true;
		txtPrivateStore.setLayoutData(keyslayoutData);

		txtPrivateStore.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePrivateStore();
				setSave(true);
				updateDirtyState();
			}
		});

		createCategory(toolkit, seccomposite, ADVANCED_SCENARIOS);
		try {
			createSecurityScenarioOptionButtons(seccomposite, SecurityPolicyUtils.getInstance()
					.getAdvancedSecurityScenarios(), managedForm, 4, body);
		} catch (IOException | JAXBException e) {
			log.error(SecurityFormMessageConstants.MESSAGE_READ_POLICY, e);
			MessageBox msg = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			msg.setMessage(SecurityFormMessageConstants.MESSAGE_POLICYFILE_READ_ERROR);
			msg.open();
		}

		Object[] aAdresult = CreateMainSection(toolkit, body, ADVANCE_CONFIGURATION, 10, 15, 600, 30, true);
		Composite rmaportInfComposite = (Composite) aAdresult[1];
		GridLayout ramportlayout = new GridLayout();
		rmaportInfComposite.setLayout(ramportlayout);

		Object[] ramBasicresult = CreateMainSection(toolkit, rmaportInfComposite, SECTION_RAMPART_CONFIGURATION, 10,
				20, 600, 30, true);
		Composite rampartBasic = (Composite) ramBasicresult[1];
		GridLayout ramparlayout = new GridLayout(2, false);
		rampartBasic.setLayout(ramparlayout);
		Section ramBasicSec = (Section) ramBasicresult[0];
		ramBasicSec.setExpanded(false);

		createRampartConfigUIs(managedForm, rampartBasic);

		enresult = CreateRampartSection(toolkit, rmaportInfComposite, SECTION_ENCRYPTION_PROPERTIES, 10, 20, 600,
				30, true);
		Composite encryptionComposite = (Composite) enresult[1];
		GridLayout enlayout = new GridLayout(2, false);
		encryptionComposite.setLayout(enlayout);
		Section enSec = (Section) enresult[0];
		enSec.setExpanded(false);

		signresult = CreateRampartSection(toolkit, rmaportInfComposite, SECTION_SIGNATURE_PROPOERTIES, 10, 30,
				600, 30, true);
		Composite signComposite = (Composite) signresult[1];
		GridLayout signlayout = new GridLayout(2, false);
		signComposite.setLayout(signlayout);
		Section signSec = (Section) signresult[0];
		signSec.setExpanded(false);

		String[] rmpartConfigs = new String[] { ORG_WSO2_CARBON_SECURITY_CRYPTO_ALIAS + ALIAS,
				ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE + PRIVATESTORE, ORG_WSO2_STRATOS_TENANT_ID + TENANT_ID,
				ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES + TRUSTSTORES, RAMPART_CONFIG_USER + USER };
		for (String name : rmpartConfigs) {
			createRampartProperties(managedForm, encryptionComposite, name, EN);
			createRampartProperties(managedForm, signComposite, name, SIGN);
		}

		try {
			String initalContent = convertXMLFileToString(inputFile);
			updateSecurityOptionButtons(initalContent, resultService, enresult,signresult);
			updateRampartUIWithChanges(initalContent);
		} catch (JAXBException | ParserConfigurationException | SAXException | IOException e) {
			log.error(SecurityFormMessageConstants.MESSAGE_LOAD_PAGE, e);
			MessageBox msg = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			msg.setMessage(SecurityFormMessageConstants.MESSAGE_PAGE_LOADING_ERROR);
			msg.open();
		}
	}

	private Object[] CreateRampartSection(FormToolkit toolkit, final Composite body, String sectionName, final int x,
			final int y, final int width, final int height, boolean expand) {

		Object[] comp = new Object[2];
		final Section sctnCreate = toolkit.createSection(body, Section.TWISTIE | Section.TITLE_BAR);
		sctnCreate.setBounds(x, y, width, height);
		toolkit.paintBordersFor(sctnCreate);
		sctnCreate.setText(sectionName);
		sctnCreate.setExpanded(expand);
		sctnCreate.setVisible(false);
		/*
		 * GridData layoutData = new GridData(); layoutData.minimumWidth = 600;
		 * layoutData.horizontalAlignment = SWT.FILL;
		 * layoutData.grabExcessHorizontalSpace = true;
		 * sctnCreate.setLayoutData(layoutData);
		 */
		sctnCreate.addExpansionListener(new IExpansionListener() {

			@Override
			public void expansionStateChanging(ExpansionEvent e) {
			}

			@Override
			public void expansionStateChanged(ExpansionEvent e) {

			}
		});
		comp[0] = sctnCreate;
		Composite composite = toolkit.createComposite(sctnCreate, SWT.NULL);
		toolkit.paintBordersFor(composite);
		sctnCreate.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		comp[1] = composite;

		return comp;
	}

	private Object[] CreateSecuritySection(FormToolkit toolkit, final Composite body, String sectionName, final int x,
			final int y, final int width, final int height, boolean expand) {

		Object[] comp = new Object[2];
		final Section sctnCreate = toolkit.createSection(body, Section.TWISTIE | Section.TITLE_BAR);
		sctnCreate.setBounds(x, y, width, height);
		toolkit.paintBordersFor(sctnCreate);
		sctnCreate.setText(sectionName);
		sctnCreate.setExpanded(expand);
		sctnCreate.setVisible(false);
		/*
		 * GridData layoutData = new GridData(); layoutData.minimumWidth = 600;
		 * layoutData.horizontalAlignment = SWT.FILL;
		 * layoutData.grabExcessHorizontalSpace = true;
		 * sctnCreate.setLayoutData(layoutData);
		 */
		sctnCreate.addExpansionListener(new IExpansionListener() {

			@Override
			public void expansionStateChanging(ExpansionEvent e) {
			}

			@Override
			public void expansionStateChanged(ExpansionEvent e) {

			}
		});
		comp[0] = sctnCreate;
		Composite composite = toolkit.createComposite(sctnCreate, SWT.NULL);
		toolkit.paintBordersFor(composite);
		sctnCreate.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		comp[1] = composite;

		return comp;
	}

	private void createRampartConfigUIs(IManagedForm managedForm, Composite rampartBasic) {

		managedForm.getToolkit().createLabel(rampartBasic, LABEL_USER);
		txtRampartUser = managedForm.getToolkit().createText(rampartBasic, "");
		GridData rmUserlayoutData = new GridData();
		rmUserlayoutData.minimumWidth = 200;
		rmUserlayoutData.horizontalAlignment = SWT.FILL;
		rmUserlayoutData.grabExcessHorizontalSpace = true;
		txtRampartUser.setLayoutData(rmUserlayoutData);
		rampartControlMap.put(RAMPART_USER, txtRampartUser);

		txtRampartUser.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				rampartDataMap.put(RAMPART_USER, txtRampartUser.getText());
				setSave(true);
				updateDirtyState();
			}
		});

		managedForm.getToolkit().createLabel(rampartBasic, LABEL_ENCRYPTION_USER);
		txtRampartEncryptionUser = managedForm.getToolkit().createText(rampartBasic, "");
		GridData encryptionUserlayoutData = new GridData();
		encryptionUserlayoutData.minimumWidth = 200;
		encryptionUserlayoutData.horizontalAlignment = SWT.FILL;
		encryptionUserlayoutData.grabExcessHorizontalSpace = true;
		txtRampartEncryptionUser.setLayoutData(encryptionUserlayoutData);
		rampartControlMap.put(RAMPART_ENCRYPTION_USER, txtRampartEncryptionUser);

		txtRampartEncryptionUser.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				rampartDataMap.put(RAMPART_ENCRYPTION_USER, txtRampartEncryptionUser.getText());
				setSave(true);
				updateDirtyState();
			}
		});

		String[] values = new String[] { VALUE_FALSE, VALUE_TRUE };
		managedForm.getToolkit().createLabel(rampartBasic, LABEL_PRECISION);
		cmbRampartTimestampPrecision = new Combo(rampartBasic, SWT.READ_ONLY);
		cmbRampartTimestampPrecision.setItems(values);
		GridData timestampPrecisionInMillisecondslayoutData = new GridData();
		timestampPrecisionInMillisecondslayoutData.minimumWidth = 200;
		timestampPrecisionInMillisecondslayoutData.horizontalAlignment = SWT.FILL;
		timestampPrecisionInMillisecondslayoutData.grabExcessHorizontalSpace = true;
		cmbRampartTimestampPrecision.setLayoutData(timestampPrecisionInMillisecondslayoutData);
		rampartControlMap.put(RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS, cmbRampartTimestampPrecision);

		cmbRampartTimestampPrecision.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				rampartDataMap.put(RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS,
						cmbRampartTimestampPrecision.getItem(cmbRampartTimestampPrecision.getSelectionIndex()));
				setSave(true);
				updateDirtyState();
			}
		});

		managedForm.getToolkit().createLabel(rampartBasic, LABEL_TIMESTAMP_TTL);
		txtRampartMinTTL = managedForm.getToolkit().createText(rampartBasic, " ");
		GridData timestampTTLlayoutData = new GridData();
		timestampTTLlayoutData.minimumWidth = 200;
		timestampTTLlayoutData.horizontalAlignment = SWT.FILL;
		timestampTTLlayoutData.grabExcessHorizontalSpace = true;
		txtRampartMinTTL.setLayoutData(timestampTTLlayoutData);
		rampartControlMap.put(RAMPART_TIMESTAMP_TTL, txtRampartMinTTL);

		txtRampartMinTTL.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				rampartDataMap.put(RAMPART_TIMESTAMP_TTL, txtRampartMinTTL.getText());
				setSave(true);
				updateDirtyState();
			}
		});

		managedForm.getToolkit().createLabel(rampartBasic, LABEL_TIMESTAMP_MAX);
		txtRampartTimestampMaxSkew = managedForm.getToolkit().createText(rampartBasic, "");
		GridData timestampMaxSkewlayoutData = new GridData();
		timestampMaxSkewlayoutData.minimumWidth = 200;
		timestampMaxSkewlayoutData.horizontalAlignment = SWT.FILL;
		timestampMaxSkewlayoutData.grabExcessHorizontalSpace = true;
		txtRampartTimestampMaxSkew.setLayoutData(timestampMaxSkewlayoutData);
		rampartControlMap.put(RAMPART_TIMESTAMP_MAX_SKEW, txtRampartTimestampMaxSkew);

		txtRampartTimestampMaxSkew.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				rampartDataMap.put(RAMPART_TIMESTAMP_MAX_SKEW, txtRampartTimestampMaxSkew.getText());
				setSave(true);
				updateDirtyState();
			}
		});

		managedForm.getToolkit().createLabel(rampartBasic, LABEL_TIMESTAMP_STRICT);
		cmbRampartTimestampStrict = new Combo(rampartBasic, SWT.READ_ONLY);
		cmbRampartTimestampStrict.setItems(values);
		GridData timestampStrictlayoutData = new GridData();
		timestampStrictlayoutData.minimumWidth = 200;
		timestampStrictlayoutData.horizontalAlignment = SWT.FILL;
		timestampStrictlayoutData.grabExcessHorizontalSpace = true;
		cmbRampartTimestampStrict.setLayoutData(timestampStrictlayoutData);
		rampartControlMap.put(RAMPART_TIMESTAMP_STRICT, cmbRampartTimestampStrict);

		cmbRampartTimestampStrict.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				rampartDataMap.put(RAMPART_TIMESTAMP_STRICT,
						cmbRampartTimestampStrict.getItem(cmbRampartTimestampStrict.getSelectionIndex()));
				setSave(true);
				updateDirtyState();
			}
		});

		managedForm.getToolkit().createLabel(rampartBasic, LABEL_TOKEN_STORE_CLASS);
		txtRampartTokenStoreClass = managedForm.getToolkit().createText(rampartBasic, "");
		GridData tokenStoreClasslayoutData = new GridData();
		tokenStoreClasslayoutData.minimumWidth = 200;
		tokenStoreClasslayoutData.horizontalAlignment = SWT.FILL;
		tokenStoreClasslayoutData.grabExcessHorizontalSpace = true;
		txtRampartTokenStoreClass.setLayoutData(tokenStoreClasslayoutData);
		rampartControlMap.put(RAMPART_TOKEN_STORE_CLASS, txtRampartTokenStoreClass);

		txtRampartTokenStoreClass.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				rampartDataMap.put(RAMPART_TOKEN_STORE_CLASS, txtRampartTokenStoreClass.getText());
				setSave(true);
				updateDirtyState();
			}
		});

		managedForm.getToolkit().createLabel(rampartBasic, LABEL_NONCELIFETIME);
		txtRampartNonceLifeTime = managedForm.getToolkit().createText(rampartBasic, "");
		GridData nonceLifeTimelayoutData = new GridData();
		nonceLifeTimelayoutData.minimumWidth = 200;
		nonceLifeTimelayoutData.horizontalAlignment = SWT.FILL;
		nonceLifeTimelayoutData.grabExcessHorizontalSpace = true;
		txtRampartNonceLifeTime.setLayoutData(nonceLifeTimelayoutData);
		rampartControlMap.put(RAMPART_NONCE_LIFE_TIME, txtRampartNonceLifeTime);

		txtRampartNonceLifeTime.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				rampartDataMap.put(RAMPART_NONCE_LIFE_TIME, txtRampartNonceLifeTime.getText());
				setSave(true);
				updateDirtyState();
			}
		});
	}

	/**
	 * Saves the configuration to the file
	 * 
	 * @throws InterruptedException
	 */
	private void save() throws InterruptedException {

		try {
			// Adds the policy
			addPolicy();

			// Update data maps with user inputs
			updateDataMapsWithUserInputs();

			// Updates the source view
			updateSourceConfiguration();

			// Saves the final output to the inputFile
			saveFinalConfigToFile();

			RefreshProject();

			/*
			 * display.asyncExec(new Runnable() {
			 * 
			 * @Override public void run() { try { RefreshProject(); } catch
			 * (CoreException e) { log.error("Error in refresing the project",
			 * e); } } });
			 */

		} catch (JAXBException | IOException | CoreException | ParserConfigurationException | SAXException
				| TransformerException e) {
			log.error("Saving Error", e);
			MessageBox msg = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			msg.setMessage(SecurityFormMessageConstants.MESSAGE_SAVE_ERROR);
			msg.open();
		}
	}

	/**
	 * Updates the source view at page change
	 * 
	 * @throws InterruptedException
	 */
	private void updateSource() throws InterruptedException {

		try {
			// Adds the policy
			addPolicy();
			// Update data maps with user inputs
			updateDataMapsWithUserInputs();
			// Updates the source view
			updateSourceConfiguration();

		} catch (JAXBException | IOException | CoreException | ParserConfigurationException | SAXException
				| TransformerException e) {
			log.error(SecurityFormMessageConstants.MESSAGE_SAVE, e);
			MessageBox msg = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			msg.setMessage(SecurityFormMessageConstants.MESSAGE_SAVE_ERROR);
			msg.open();
		}
	}

	/**
	 * Adds the policy
	 * 
	 * @throws JAXBException
	 * @throws IOException
	 * @throws PropertyException
	 * @throws CoreException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	private void addPolicy() throws JAXBException, IOException, PropertyException, CoreException,
			ParserConfigurationException, SAXException, TransformerException {

		SecurityTemplateUtil secTemplateUtil = new SecurityTemplateUtil();
		String filename = POLICIES + policyFileName;
		policyTemplateFile = secTemplateUtil.getResourceFile(filename);
		String content = convertXMLFileToString(policyTemplateFile);

		policyTemplateStream = new ByteArrayInputStream(content.getBytes());
		closeInputStream(policyTemplateStream);

		if (policyTemplateFile != null) {
			Unmarshaller pUnmarshaller = getUnmarsheller();
			policyObject = (Policy2) pUnmarshaller.unmarshal(policyTemplateStream);

		}
	}

	/**
	 * Saves the data maps with user values
	 */
	private void updateDataMapsWithUserInputs() {

		Set<String> keySet = encryptControlMap.keySet();
		for (String key : keySet) {
			Object control = encryptControlMap.get(key);
			if (control instanceof Text) {
				Text controlText = (Text) control;
				encryptDataMap.put(key, controlText.getText());
			}
		}
		keySet = signControlMap.keySet();
		for (String key : keySet) {
			Object control = signControlMap.get(key);
			if (control instanceof Text) {
				Text controlText = (Text) control;
				signDataMap.put(key, controlText.getText());
			}
		}
	}

	/**
	 * Updates the source with changes
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void updateSourceConfiguration() throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilder dBuilder = getDocumentBuilder();
		doc = dBuilder.parse(policyTemplateFile);

		policyID = policyObject.getId();

		boolean isKerberossignandencrypt = policyID.equals(KERBEROSSIGNANDENCRYPT);

		Node nrampart = doc.getElementsByTagName(RAMPART_CONFIG).item(0);
		rampart = (Element) nrampart;

		if (!isKerberossignandencrypt) {
			Node user = rampart.getElementsByTagName(RAMPART_USER).item(0);
			if (user != null) {
				user.setTextContent(rampartDataMap.get(RAMPART_USER));
			}

			Node encryptionUser = rampart.getElementsByTagName(RAMPART_ENCRYPTION_USER).item(0);
			if (encryptionUser != null && StringUtils.isNotBlank(rampartDataMap.get(RAMPART_ENCRYPTION_USER))) {
				encryptionUser.setTextContent(rampartDataMap.get(RAMPART_ENCRYPTION_USER));
			}
		}

		Node timestampPrecisionInMilliseconds = rampart.getElementsByTagName(
				RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS).item(0);
		if (timestampPrecisionInMilliseconds != null
				&& StringUtils.isNotBlank(rampartDataMap.get(RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS))) {
			timestampPrecisionInMilliseconds.setTextContent(rampartDataMap
					.get(RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS));
		}

		Node timestampTTL = rampart.getElementsByTagName(RAMPART_TIMESTAMP_TTL).item(0);
		if (timestampTTL != null && StringUtils.isNotBlank(rampartDataMap.get(RAMPART_TIMESTAMP_TTL))) {
			timestampTTL.setTextContent(rampartDataMap.get(RAMPART_TIMESTAMP_TTL));
		}

		Node timestampMaxSkew = rampart.getElementsByTagName(RAMPART_TIMESTAMP_MAX_SKEW).item(0);
		if (timestampMaxSkew != null && StringUtils.isNotBlank(rampartDataMap.get(RAMPART_TIMESTAMP_MAX_SKEW))) {
			timestampMaxSkew.setTextContent(rampartDataMap.get(RAMPART_TIMESTAMP_MAX_SKEW));
		}

		Node timestampStrict = rampart.getElementsByTagName(RAMPART_TIMESTAMP_STRICT).item(0);
		if (timestampStrict != null && StringUtils.isNotBlank(rampartDataMap.get(RAMPART_TIMESTAMP_STRICT))) {
			timestampStrict.setTextContent(rampartDataMap.get(RAMPART_TIMESTAMP_STRICT));
		}

		if (!isKerberossignandencrypt) {
			Node tokenStoreClass = rampart.getElementsByTagName(RAMPART_TOKEN_STORE_CLASS).item(0);
			tokenStoreClass.setTextContent(rampartDataMap.get(RAMPART_TOKEN_STORE_CLASS));
		}

		Node nonceLifeTime = rampart.getElementsByTagName(RAMPART_NONCE_LIFE_TIME).item(0);
		if (nonceLifeTime != null && StringUtils.isNotBlank(rampartDataMap.get(RAMPART_NONCE_LIFE_TIME))) {
			nonceLifeTime.setTextContent(rampartDataMap.get(RAMPART_NONCE_LIFE_TIME));
		}

		if (!isKerberossignandencrypt) {
			Node encryptionCrypto = rampart.getElementsByTagName(RAMPART_ENCRYPTION_CRYPTO).item(0);
			if (encryptionCrypto != null) {
				setenCryto(encryptionCrypto, encryptDataMap);
			}

			Node signatureCrypto = rampart.getElementsByTagName(RAMPART_SIGNATURE_CRYPTO).item(0);
			if (signatureCrypto != null) {
				setenCryto(signatureCrypto, signDataMap);
			}
		} else {
			Node kerberosConfig = rampart.getElementsByTagName(RAMPART_KERBEROS_CONFIG).item(0);
			if (kerberosConfig != null) {
				setKerberosConfig(kerberosConfig);
			}
		}
	}

	/**
	 * Gets the document builder
	 * 
	 * @return dBuilder document builder
	 * @throws ParserConfigurationException
	 */
	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder;
	}

	/**
	 * Gets the transformer
	 * 
	 * @return transformer transformer
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 */
	private Transformer getTransformer() throws TransformerFactoryConfigurationError, TransformerConfigurationException {

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		return transformer;
	}

	/**
	 * Closes the input stream
	 * 
	 * @param stream
	 */
	private void closeInputStream(InputStream stream) {

		try {
			stream.close();
		} catch (IOException e) {
			log.error("Error in closing the input stream", e);
		}
	}

	/**
	 * Gets the unmarshaller
	 * 
	 * @return unmarshaller unmarshaller
	 * @throws JAXBException
	 */
	private Unmarshaller getUnmarsheller() throws JAXBException {

		JAXBContext jaxbContext = JAXBContext.newInstance(Policy2.class);
		Unmarshaller uUnmarshaller = jaxbContext.createUnmarshaller();
		return uUnmarshaller;
	}

	/**
	 * Saves the final configurations to the file
	 * 
	 * @throws TransformerException
	 */
	private void saveFinalConfigToFile() throws TransformerException {

		Transformer transformer = getTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(inputFile);
		transformer.transform(source, result);
	}

	/**
	 * Gets the updated content of the source
	 * 
	 * @return content
	 * @throws TransformerException
	 */
	private String getUpdatedContent() throws TransformerException {

		Transformer transformer = getTransformer();
		StringWriter stw = new StringWriter();
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, new StreamResult(stw));
		return stw.toString();

	}

	/**
	 * Updates the Private store and trust store
	 */
	private void updatePrivateStore() {

		if (encryptControlMap.size() > 0 && signControlMap.size() > 0) {
			// encryption properties
			encryptControlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE).setText(txtPrivateStore.getText());
			encryptControlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES).setText(txtPrivateStore.getText());

			// signature properties
			signControlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE).setText(txtPrivateStore.getText());
			signControlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES).setText(txtPrivateStore.getText());
		}
	}

	/**
	 * Updates the RampartUI with new values
	 * 
	 * @param alise
	 *            alias value
	 * @param serviceMetaFile
	 *            Service meta file
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private void updateRampartUIWithChanges(String source) throws ParserConfigurationException, SAXException,
			IOException {

		// Gets rampart config values from the source view

		InputStream rampartStream = new ByteArrayInputStream(source.getBytes());
		getRampartValuesFromSource(rampartStream);
		closeInputStream(rampartStream);

		// Gets rampart encryption and sign values from source view
		InputStream cryptotStream = new ByteArrayInputStream(source.getBytes());
		getRampartEncryptionAndSignValuesFromSource(cryptotStream);
		closeInputStream(cryptotStream);

		// updates rampart ui
		Text txtRampartProperties = (Text) rampartControlMap.get(RAMPART_USER);
		if (rampartDataMap.get(RAMPART_USER) != null && txtRampartProperties != null) {
			txtRampartProperties.setText(rampartDataMap.get(RAMPART_USER));
		} else if (txtRampartProperties != null) {
			txtRampartProperties.setText("");
		}

		txtRampartProperties = (Text) rampartControlMap.get(RAMPART_ENCRYPTION_USER);
		if (rampartDataMap.get(RAMPART_ENCRYPTION_USER) != null && txtRampartProperties != null) {
			txtRampartProperties.setText(rampartDataMap.get(RAMPART_ENCRYPTION_USER));
		} else if (txtRampartProperties != null) {
			txtRampartProperties.setText("");
		}

		txtRampartProperties = (Text) rampartControlMap.get(RAMPART_TIMESTAMP_TTL);
		if (rampartDataMap.get(RAMPART_TIMESTAMP_TTL) != null && txtRampartProperties != null) {
			txtRampartProperties.setText(rampartDataMap.get(RAMPART_TIMESTAMP_TTL));
		} else if (txtRampartProperties != null) {
			txtRampartProperties.setText("");
		}

		txtRampartProperties = (Text) rampartControlMap.get(RAMPART_TIMESTAMP_MAX_SKEW);
		if (rampartDataMap.get(RAMPART_TIMESTAMP_MAX_SKEW) != null && txtRampartProperties != null) {
			txtRampartProperties.setText(rampartDataMap.get(RAMPART_TIMESTAMP_MAX_SKEW));
		} else if (txtRampartProperties != null) {
			txtRampartProperties.setText("");
		}

		txtRampartProperties = (Text) rampartControlMap.get(RAMPART_TOKEN_STORE_CLASS);
		if (rampartDataMap.get(RAMPART_TOKEN_STORE_CLASS) != null && txtRampartProperties != null) {
			txtRampartProperties.setText(rampartDataMap.get(RAMPART_TOKEN_STORE_CLASS));
		} else if (txtRampartProperties != null) {
			txtRampartProperties.setText("");
		}

		txtRampartProperties = (Text) rampartControlMap.get(RAMPART_NONCE_LIFE_TIME);
		if (rampartDataMap.get(RAMPART_NONCE_LIFE_TIME) != null && txtRampartProperties != null) {
			txtRampartProperties.setText(rampartDataMap.get(RAMPART_NONCE_LIFE_TIME));
		} else if (txtRampartProperties != null) {
			txtRampartProperties.setText("");
		}

		Combo cmbRampartTimeStampProperty = (Combo) rampartControlMap.get(RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS);
		if (rampartDataMap.get(RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS) != null
				&& cmbRampartTimeStampProperty != null) {
			if (rampartDataMap.get(RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS).equals(VALUE_FALSE)) {
				cmbRampartTimeStampProperty.select(0); // Index 0
			} else {
				cmbRampartTimeStampProperty.select(1); // Index 1
			}
		}

		cmbRampartTimeStampProperty = (Combo) rampartControlMap.get(RAMPART_TIMESTAMP_STRICT);
		if (rampartDataMap.get(RAMPART_TIMESTAMP_STRICT) != null && cmbRampartTimeStampProperty != null) {
			if (rampartDataMap.get(RAMPART_TIMESTAMP_STRICT).equals(VALUE_FALSE)) {
				cmbRampartTimeStampProperty.select(0); // Index 0
			} else {
				cmbRampartTimeStampProperty.select(1);// Index 1
			}
		}

		// encrypt values
		updateCryptoUIWithChanges(encryptDataMap, encryptControlMap);

		// sign values
		updateCryptoUIWithChanges(signDataMap, signControlMap);

	}

	/**
	 * Updates crypto UI
	 * 
	 * @param dataMap
	 *            data map
	 * @param controlMap
	 *            ui map
	 */
	private void updateCryptoUIWithChanges(Map<String, String> dataMap, Map<String, Text> controlMap) {
		if (controlMap.size() > 0) {
			if (dataMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE) != null) {
				controlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE).setText(
						dataMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE));
			} else {
				controlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_PRIVATESTORE).setText("");

			}
			if (dataMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES) != null) {
				controlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES).setText(
						dataMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES));
			} else {
				controlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_TRUSTSTORES).setText("");

			}
			if (dataMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_ALIAS) != null) {
				controlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_ALIAS).setText(
						dataMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_ALIAS));
			} else {
				controlMap.get(ORG_WSO2_CARBON_SECURITY_CRYPTO_ALIAS).setText("");

			}
			if (dataMap.get(ORG_WSO2_STRATOS_TENANT_ID) != null) {
				controlMap.get(ORG_WSO2_STRATOS_TENANT_ID).setText(dataMap.get(ORG_WSO2_STRATOS_TENANT_ID));
			} else {
				controlMap.get(ORG_WSO2_STRATOS_TENANT_ID).setText("");

			}
			if (dataMap.get(RAMPART_CONFIG_USER) != null) {
				controlMap.get(RAMPART_CONFIG_USER).setText(dataMap.get(RAMPART_CONFIG_USER));
			} else {
				controlMap.get(RAMPART_CONFIG_USER).setText("");
			}
		}
	}

	/**
	 * Updates security options on page change
	 * 
	 * @param source
	 *            content
	 * @param signresult2 
	 * @param enresult2 
	 * @param body
	 *            body
	 * @param toolkit
	 *            managed form
	 * @throws JAXBException
	 */
	private void updateSecurityOptionButtons(String source, Object[] resultService, Object[] enresult, Object[] signresult) throws JAXBException {

		Unmarshaller uUnmarshaller = getUnmarsheller();
		InputStream inputStream = new ByteArrayInputStream(source.getBytes());
		policyObject = (Policy2) uUnmarshaller.unmarshal(inputStream);
		closeInputStream(inputStream);
		if (resultService != null || enresult != null || signresult != null ) {
			Section result = (Section) resultService[0];
			Section encrypt = (Section)enresult[0];
			Section signResult = (Section) signresult[0];
			if (policyObject.getId().equals(POLICY_OBJECT_UT) || policyObject.getId().equals(KERBEROSSIGNANDENCRYPT)) {
				result.setVisible(false);
				encrypt.setVisible(false);
				signResult.setVisible(false);
			} else {
				result.setVisible(true);
				encrypt.setVisible(true);
				signResult.setVisible(true);
			}
		}
		Button button = policyeMap.get(policyObject.getId());
		if (button != null) {
			button.setSelection(true);
			policyFileName = (String) button.getData();
			selectedPolicy = SecurityPolicyUtils.getInstance().getPolicyTypeFromPolicyUUID(policyObject.getId());
		}
	}

	/**
	 * Creates RampartProperties
	 * 
	 * @param managedForm
	 *            form
	 * @param enComposite
	 *            composite
	 * @param fullname
	 *            name
	 * @param prefix
	 *            prefix
	 */
	private void createRampartProperties(IManagedForm managedForm, Composite enComposite, String fullname, String prefix) {

		String[] split = fullname.split(":");
		String name = split[1];
		managedForm.getToolkit().createLabel(enComposite, name + ":");
		Text en = managedForm.getToolkit().createText(enComposite, " ");
		GridData enlayoutData = new GridData();
		enlayoutData.minimumWidth = 200;
		enlayoutData.horizontalAlignment = SWT.FILL;
		enlayoutData.grabExcessHorizontalSpace = true;
		en.setLayoutData(enlayoutData);
		en.setData(name, split[0]);
		// en.setd
		if (EN.equals(prefix)) {
			encryptControlMap.put(split[0], en);
		} else {
			signControlMap.put(split[0], en);
		}
		en.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				setSave(true);
				updateDirtyState();
			}
		});

	}

	/**
	 * Convert XML to string
	 * 
	 * @param resourceFile
	 *            file
	 * @return
	 */
	private String convertXMLFileToString(File resourceFile) {

		StringWriter stw = new StringWriter();
		try {
			DocumentBuilder dBuilder = getDocumentBuilder();
			InputStream inputStream = new FileInputStream(resourceFile);
			org.w3c.dom.Document doc = dBuilder.parse(inputStream);
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty("omit-xml-declaration", "yes");
			serializer.transform(new DOMSource(doc), new StreamResult(stw));
		} catch (TransformerException | SAXException | IOException | ParserConfigurationException
				| TransformerFactoryConfigurationError e) {
			log.error(SecurityFormMessageConstants.MESSAGE_XML_ERROR, e);
		}
		return stw.toString();
	}

	/**
	 * Refreshes the project
	 * 
	 * @throws CoreException
	 */
	public void RefreshProject() throws CoreException {
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	/**
	 * Updates the rampart configuration data map
	 * 
	 * @param uiContentStream
	 *            input stream
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void getRampartValuesFromSource(InputStream uiContentStream) throws ParserConfigurationException,
			SAXException, IOException {

		DocumentBuilder dBuilder = getDocumentBuilder();

		doc = dBuilder.parse(uiContentStream);
		policyID = policyObject.getId();

		boolean isKerberossignandencrypt = policyID.equals(KERBEROSSIGNANDENCRYPT);

		Node nrampart = doc.getElementsByTagName(RAMPART_CONFIG).item(0);
		rampart = (Element) nrampart;

		if (!isKerberossignandencrypt) {
			Node user = rampart.getElementsByTagName(RAMPART_USER).item(0);
			if (user != null) {
				rampartDataMap.put(RAMPART_USER, user.getTextContent());
			}

			Node encryptionUser = rampart.getElementsByTagName(RAMPART_ENCRYPTION_USER).item(0);
			if (encryptionUser != null) {
				rampartDataMap.put(RAMPART_ENCRYPTION_USER, encryptionUser.getTextContent());
			}

		} else {
			// rampart:kerberosConfig
			Node kerberosConfig = rampart.getElementsByTagName(RAMPART_KERBEROS_CONFIG).item(0);
			if (kerberosConfig != null) {
				addRampartKerberosConfigPropertis(kerberosConfig);
			}
		}

		Node timestampPrecisionInMilliseconds = rampart.getElementsByTagName(
				RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS).item(0);
		if (timestampPrecisionInMilliseconds != null) {
			rampartDataMap.put(RAMPART_TIMESTAMP_PRECISION_IN_MILLISECONDS,
					timestampPrecisionInMilliseconds.getTextContent());
		}

		Node timestampTTL = rampart.getElementsByTagName(RAMPART_TIMESTAMP_TTL).item(0);
		if (timestampTTL != null) {
			rampartDataMap.put(RAMPART_TIMESTAMP_TTL, timestampTTL.getTextContent());
		}

		Node timestampMaxSkew = rampart.getElementsByTagName(RAMPART_TIMESTAMP_MAX_SKEW).item(0);
		if (timestampMaxSkew != null) {
			rampartDataMap.put(RAMPART_TIMESTAMP_MAX_SKEW, timestampMaxSkew.getTextContent());
		}

		Node timestampStrict = rampart.getElementsByTagName(RAMPART_TIMESTAMP_STRICT).item(0);
		if (timestampStrict != null) {
			rampartDataMap.put(RAMPART_TIMESTAMP_STRICT, timestampStrict.getTextContent());
		}

		if (!isKerberossignandencrypt) {
			Node tokenStoreClass = rampart.getElementsByTagName(RAMPART_TOKEN_STORE_CLASS).item(0);
			if (tokenStoreClass != null) {
				rampartDataMap.put(RAMPART_TOKEN_STORE_CLASS, tokenStoreClass.getTextContent());
			}
		}

		Node nonceLifeTime = rampart.getElementsByTagName(RAMPART_NONCE_LIFE_TIME).item(0);
		if (nonceLifeTime != null) {
			rampartDataMap.put(RAMPART_NONCE_LIFE_TIME, nonceLifeTime.getTextContent());
		}
	}

	/**
	 * Updates the encryption data map
	 * 
	 * @param uiContentStream
	 *            input stream
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void getRampartEncryptionAndSignValuesFromSource(InputStream uiContentStream)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilder dBuilder = getDocumentBuilder();
		doc = dBuilder.parse(uiContentStream);
		policyID = policyObject.getId();

		boolean isKerberossignandencrypt = policyID.equals(KERBEROSSIGNANDENCRYPT);

		Node nrampart = doc.getElementsByTagName(RAMPART_CONFIG).item(0);
		rampart = (Element) nrampart;

		if (!isKerberossignandencrypt) {
			Node encryptionCrypto = rampart.getElementsByTagName(RAMPART_ENCRYPTION_CRYPTO).item(0);
			if (encryptionCrypto != null) {
				encryptDataMap = addRampartCryptoProperties(encryptionCrypto);
			}

			// rampart:signatureCrypto
			Node signatureCrypto = rampart.getElementsByTagName(RAMPART_SIGNATURE_CRYPTO).item(0);
			if (signatureCrypto != null) {
				signDataMap = addRampartCryptoProperties(signatureCrypto);
			}
		}

	}

	/**
	 * Add rampart:encryptionCrypto or rampart:signatureCrypto properties.
	 * 
	 * @param crypto
	 *            crypto node
	 */
	private static Map<String, String> addRampartCryptoProperties(Node crypto) {

		Map<String, String> cryptoMap = new HashMap<>();
		Node encrypto = ((Element) crypto).getElementsByTagName(RAMPART_CRYPTO).item(0);

		NodeList list = ((Element) encrypto).getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (RAMPART_PROPERTY.equals(node.getNodeName())) {
				Element eElement = (Element) node;
				String attribute = eElement.getAttribute(RAMPART_PROPERTY_NAME);
				cryptoMap.put(attribute, eElement.getTextContent());
			}
		}
		return cryptoMap;
	}

	/**
	 * Add rampart:kerberosConfig properties.
	 * 
	 * @param kerberosConfig
	 *            kerberos node
	 */
	private static void addRampartKerberosConfigPropertis(Node kerberosConfig) {

		NodeList list = ((Element) kerberosConfig).getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (RAMPART_PROPERTY.equals(node.getNodeName())) {
				Element eElement = (Element) node;
				String attribute = eElement.getAttribute(RAMPART_PROPERTY_NAME);
				rampartDataMap.put(attribute, eElement.getTextContent());
			}
		}
	}

	/**
	 * Sets the encryption and sign values
	 * 
	 * @param encryptionCrypto
	 *            node encrypto
	 * @param cryptoMap
	 *            map
	 */
	private static void setenCryto(Node encryptionCrypto, Map<String, String> cryptoMap) {

		Node encrypto = ((Element) encryptionCrypto).getElementsByTagName(RAMPART_CRYPTO).item(0);
		NodeList list = ((Element) encrypto).getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node != null && RAMPART_PROPERTY.equals(node.getNodeName())) {
				Element eElement = (Element) node;
				String attribute = eElement.getAttribute(RAMPART_PROPERTY_NAME);
				if (StringUtils.isNotBlank(attribute)) {
					node.setTextContent(cryptoMap.get(attribute));
				}
			}
		}
	}

	/**
	 * Sets the kerberos configs
	 * 
	 * @param kerberosConfig
	 *            kerbeos config
	 */
	private static void setKerberosConfig(Node kerberosConfig) {

		NodeList list = ((Element) kerberosConfig).getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node != null && RAMPART_PROPERTY.equals(node.getNodeName())) {
				Element eElement = (Element) node;
				String attribute = eElement.getAttribute(RAMPART_PROPERTY_NAME);
				if (StringUtils.isNotBlank(attribute)) {
					node.setTextContent(eElement.getTextContent());
				}
			}
		}
	}

	/**
	 * Creates the security items
	 * 
	 * @param seccomposite
	 *            composite
	 * @param names
	 *            names
	 * @param managedForm
	 *            form
	 * @param i
	 *            int value
	 */
	private void createSecurityScenarioOptionButtons(Composite seccomposite, String[] names, IManagedForm managedForm,
			int i, Composite body) throws IOException, JAXBException {

		for (String name : names) {
			i++;
			final Button secBtn = new Button(seccomposite, SWT.RADIO);
			secBtn.setText("");
			secBtn.setToolTipText(name);

			String fileName = FILE_PREFIX + i + FILE_POSTFIX;
			secBtn.setData(fileName);
			secBtn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					policyFileName = (String) secBtn.getData();
					selectedPolicy = secBtn.getToolTipText();
					setSave(true);
					updateDirtyState();
					if (resultService != null || enresult != null || signresult != null) {
						Section result = (Section) resultService[0];
						Section encrypt = (Section) enresult[0];
						Section  signResult = (Section) signresult[0];
						if (selectedPolicy.equals(POLICY_UT) || selectedPolicy.equals(POLICY_KERBEROS)) {
							result.setVisible(false);
							encrypt.setVisible(false);
							signResult.setVisible(false);
						} else {
							result.setVisible(true);
							encrypt.setVisible(true);
							signResult.setVisible(true);
							
						}
					}

					policyOneUserRolesButton.setVisible(false);
					policySevenUserRolesButton.setVisible(false);
					policyEightUserRolesButton.setVisible(false);
					policyFourteenUserRolesButton.setVisible(false);
					policyFifteenUserRolesButton.setVisible(false);
				}
			});

			String filename = POLICIES + fileName;
			SecurityTemplateUtil qoSTemplateUtil = new SecurityTemplateUtil();
			File resourceFile = qoSTemplateUtil.getResourceFile(filename);
			if (resourceFile != null) {
				Unmarshaller pUnmarshaller = getUnmarsheller();
				Policy2 policy2 = (Policy2) pUnmarshaller.unmarshal(resourceFile);
				policyeMap.put(policy2.getId(), secBtn);
			}

			final ToolTip tip = new ToolTip(seccomposite.getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);
			tip.setMessage(TIP_MESSAGE);

			Hyperlink createHyperlink = managedForm.getToolkit().createHyperlink(seccomposite, name, SWT.RADIO);
			createHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					// Fixing TOOLS-2293.
					// tip.setVisible(true);
				}

			});

			if (SecurityPolicies.POLICY_TYPE_1.equals(name)) {
				GridData policyLinkGrdiData = new GridData();
				policyLinkGrdiData.horizontalAlignment = GridData.BEGINNING;
				policyLinkGrdiData.grabExcessHorizontalSpace = true;
				policyLinkGrdiData.horizontalSpan = 3;
				createHyperlink.setLayoutData(policyLinkGrdiData);

				policyOneUserRolesButton = new Button(seccomposite, SWT.NONE);
				policyOneUserRolesButton.setText(USER_ROLE);
				policyOneUserRolesButton.setVisible(false);
				policyOneUserRolesButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {

					}
				});

				GridData userRolesButtonGridData = new GridData();
				userRolesButtonGridData.horizontalAlignment = GridData.BEGINNING;
				userRolesButtonGridData.grabExcessHorizontalSpace = false;
				policyOneUserRolesButton.setLayoutData(userRolesButtonGridData);
			} else if (SecurityPolicies.POLICY_TYPE_7.equals(name)) {
				GridData policyLinkGrdiData = new GridData();
				policyLinkGrdiData.horizontalAlignment = GridData.BEGINNING;
				policyLinkGrdiData.grabExcessHorizontalSpace = true;
				policyLinkGrdiData.horizontalSpan = 3;
				createHyperlink.setLayoutData(policyLinkGrdiData);

				policySevenUserRolesButton = new Button(seccomposite, SWT.NONE);
				policySevenUserRolesButton.setText(USER_ROLE);
				policySevenUserRolesButton.setVisible(false);
				policySevenUserRolesButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {

					}
				});

				GridData userRolesButtonGridData = new GridData();
				userRolesButtonGridData.horizontalAlignment = GridData.BEGINNING;
				userRolesButtonGridData.grabExcessHorizontalSpace = false;
				policySevenUserRolesButton.setLayoutData(userRolesButtonGridData);
			} else if (SecurityPolicies.POLICY_TYPE_8.equals(name)) {
				GridData policyLinkGrdiData = new GridData();
				policyLinkGrdiData.horizontalAlignment = GridData.BEGINNING;
				policyLinkGrdiData.grabExcessHorizontalSpace = true;
				policyLinkGrdiData.horizontalSpan = 3;
				createHyperlink.setLayoutData(policyLinkGrdiData);

				policyEightUserRolesButton = new Button(seccomposite, SWT.NONE);
				policyEightUserRolesButton.setText(USER_ROLE);
				policyEightUserRolesButton.setVisible(false);
				policyEightUserRolesButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {

					}
				});

				GridData userRolesButtonGridData = new GridData();
				userRolesButtonGridData.horizontalAlignment = GridData.BEGINNING;
				userRolesButtonGridData.grabExcessHorizontalSpace = false;
				policyEightUserRolesButton.setLayoutData(userRolesButtonGridData);
			} else if (SecurityPolicies.POLICY_TYPE_14.equals(name)) {
				GridData policyLinkGrdiData = new GridData();
				policyLinkGrdiData.horizontalAlignment = GridData.BEGINNING;
				policyLinkGrdiData.grabExcessHorizontalSpace = true;
				policyLinkGrdiData.horizontalSpan = 3;
				createHyperlink.setLayoutData(policyLinkGrdiData);

				policyFourteenUserRolesButton = new Button(seccomposite, SWT.NONE);
				policyFourteenUserRolesButton.setText(USER_ROLE);
				policyFourteenUserRolesButton.setVisible(false);
				policyFourteenUserRolesButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {

					}
				});

				GridData userRolesButtonGridData = new GridData();
				userRolesButtonGridData.horizontalAlignment = GridData.BEGINNING;
				userRolesButtonGridData.grabExcessHorizontalSpace = false;
				policyFourteenUserRolesButton.setLayoutData(userRolesButtonGridData);
			} else if (SecurityPolicies.POLICY_TYPE_15.equals(name)) {
				GridData policyLinkGrdiData = new GridData();
				policyLinkGrdiData.horizontalAlignment = GridData.BEGINNING;
				policyLinkGrdiData.grabExcessHorizontalSpace = true;
				policyLinkGrdiData.horizontalSpan = 3;
				createHyperlink.setLayoutData(policyLinkGrdiData);

				policyFifteenUserRolesButton = new Button(seccomposite, SWT.NONE);
				policyFifteenUserRolesButton.setText(USER_ROLE);
				policyFifteenUserRolesButton.setVisible(false);
				policyFifteenUserRolesButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {

					}
				});

				GridData userRolesButtonGridData = new GridData();
				userRolesButtonGridData.horizontalAlignment = GridData.BEGINNING;
				userRolesButtonGridData.grabExcessHorizontalSpace = false;
				policyFifteenUserRolesButton.setLayoutData(userRolesButtonGridData);
			} else {
				GridData policyLinkGrdiData = new GridData();
				policyLinkGrdiData.horizontalAlignment = GridData.BEGINNING;
				policyLinkGrdiData.horizontalSpan = 4;
				createHyperlink.setLayoutData(policyLinkGrdiData);
			}

		}
	}

	/**
	 * Create sections
	 * 
	 * @param toolkit
	 *            form
	 * @param body
	 *            body
	 * @param sectionName
	 *            name of the section
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param expand
	 *            boolean
	 * @return object
	 */
	private Object[] CreateMainSection(FormToolkit toolkit, final Composite body, String sectionName, final int x,
			final int y, final int width, final int height, boolean expand) {

		Object[] comp = new Object[2];
		final Section sctnCreate = toolkit.createSection(body, Section.TWISTIE | Section.TITLE_BAR);
		sctnCreate.setBounds(x, y, width, height);
		toolkit.paintBordersFor(sctnCreate);
		sctnCreate.setText(sectionName);
		sctnCreate.setExpanded(expand);
		/*
		 * GridData layoutData = new GridData(); layoutData.minimumWidth = 600;
		 * layoutData.horizontalAlignment = SWT.FILL;
		 * layoutData.grabExcessHorizontalSpace = true;
		 * sctnCreate.setLayoutData(layoutData);
		 */
		sctnCreate.addExpansionListener(new IExpansionListener() {

			@Override
			public void expansionStateChanging(ExpansionEvent e) {
			}

			@Override
			public void expansionStateChanged(ExpansionEvent e) {

			}
		});
		comp[0] = sctnCreate;
		Composite composite = toolkit.createComposite(sctnCreate, SWT.NULL);
		toolkit.paintBordersFor(composite);
		sctnCreate.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		comp[1] = composite;

		return comp;
	}

	/**
	 * Create contents of category
	 * 
	 * @param toolkit
	 *            form
	 * @param composite
	 *            composite
	 * @param category
	 *            category
	 */
	private void createCategory(FormToolkit toolkit, Composite composite, String category) {

		Label lblcategory = toolkit.createLabel(composite, category, SWT.NONE);
		lblcategory.setFont(SWTResourceManager.getFont(SANS, 10, SWT.BOLD));
		GridData gd_category = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
		gd_category.verticalIndent = 10;
		lblcategory.setLayoutData(gd_category);

	}

	/**
	 * Saves the content to the file
	 */
	public void doPageSave() {

		try {
			setSave(false);
			((SecurityFormEditor) getEditor()).setDirty(false);
			updateDirtyState();
			save();
		} catch (Exception e) {
			log.error("Cannot save the content", e);
			MessageBox msg = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			msg.setMessage(SecurityFormMessageConstants.MESSAGE_SERIALIZATION_SAVE_ERROR);
			msg.open();
		}
	}

	/**
	 * Update the source at page change
	 * 
	 * @return updated source as an input stream
	 * @throws InterruptedException
	 */
	public String doSourceUpdate() {

		String updatedcontent = null;
		try {
			updateSource();
			updatedcontent = getUpdatedContent();
		} catch (InterruptedException | TransformerException e) {
			log.error("Error in updating the source view", e);
			MessageBox msg = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			msg.setMessage(SecurityFormMessageConstants.MESSAGE_UPDATE_SOURCE_ERROR);
			msg.open();
		}
		return updatedcontent;
	}

	public void updateDirtyState() {

		formEditor.setDirty(isSave());
		firePropertyChange(PROP_DIRTY);
		formEditor.editorDirtyStateChanged();
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}

	public boolean isSave() {
		return isSave;
	}

	/**
	 * Update the UI at page change
	 * 
	 * @param source
	 * @throws JAXBException
	 */
	public void updateUI(String source) {

		try {
			updateSecurityOptionButtons(source, resultService, enresult, signresult);
			updateRampartUIWithChanges(source);
		} catch (JAXBException | ParserConfigurationException | SAXException | IOException e) {
			log.error("Error in loading page", e);
			MessageBox msg = new MessageBox(getSite().getShell(), SWT.ICON_ERROR);
			msg.setMessage(SecurityFormMessageConstants.MESSAGE_PAGE_LOADING_ERROR);
			msg.open();
		}
	}
}