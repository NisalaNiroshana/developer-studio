/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.developerstudio.eclipse.carbonserver44.operations;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerPort;
import org.wso2.developerstudio.eclipse.carbonserver44.util.CarbonServer44Utils;
import org.wso2.developerstudio.eclipse.carbonserver44.util.CarbonServerConstants;
import org.wso2.developerstudio.eclipse.server.base.core.ServerController;

@SuppressWarnings("restriction")
public class CommonOperations {
	private static final String ANY_FILE_UPLOADER_URL="/fileupload/*";
	private static final String TOOLS_FILE_UPLOADER_URL="/fileupload/tools";
	private static final String VALIDATOR_FILE_UPLOADER_URL="/fileupload/aar_mar_validator";
	private static final String TOOLS_ANY_FILE_UPLOAD_URL="/fileupload/toolsAny";

	public static String uploadFile(File resourceUrl,String validatorUrl,String url) throws HttpException, IOException{
		File f = resourceUrl;
		PostMethod filePost = new PostMethod(url);
		Part part; 
		if (validatorUrl==null)
			part= new FilePart(f.getName(),f);
		else
			part= new FilePart(validatorUrl,f.getName(),f);
		filePost.setRequestEntity(new MultipartRequestEntity(new Part[]{part}, filePost.getParams()));
		HttpClient client = new HttpClient();
		//filePost.setFollowRedirects(true);
		int status = client.executeMethod(filePost);
		String resultUUid=null;
        resultUUid = filePost.getResponseBodyAsString();
        filePost.releaseConnection();
		return resultUUid;
	}
	
	public static String getLocalServerPort(IServer server){
		ServerPort[] serverPorts = ServerController.getInstance().getServerManager().getServerPorts(server);
		int httpPort=0;
		int offSet=0;
		for(ServerPort p:serverPorts){
			int i = CarbonServerConstants.portCaptions.indexOf(p.getName());
			if (i!=-1 && CarbonServerConstants.portIds.get(i).equals("carbon.http")){
				httpPort=p.getPort();
			}else if (i!=-1 && CarbonServerConstants.portIds.get(i).equals("carbon.offset")){
				offSet=p.getPort();
			}
		}
		//WSASServer31Utils.setTrustoreProperties(server);
		return "http://"+server.getHost()+":"+(httpPort+offSet);
	} 
	
	public static String getAnyFileUploaderUrl(IServer server){
		return getLocalServerPort(server)+ANY_FILE_UPLOADER_URL;
	}
	
	public static String getToolsUploadUrl(IServer server){
		return getLocalServerPort(server)+TOOLS_FILE_UPLOADER_URL;
	}
	
	public static String getValidatorFileUploaderUrl(IServer server){
		return getLocalServerPort(server)+VALIDATOR_FILE_UPLOADER_URL;
	}
	
	public static String getToolsAnyFileUploaderUrl(IServer server){
		return getLocalServerPort(server)+TOOLS_ANY_FILE_UPLOAD_URL;
	}
	
	public static IPath getWSASHome(IServer server){
    	return new Path(CarbonServer44Utils.resolveProperties(server, "carbon.home"));
	}
}
