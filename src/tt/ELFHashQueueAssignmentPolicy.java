/* HostnameQueueAssignmentPolicy
*
* $Id: HostnameQueueAssignmentPolicy.java 3838 2005-09-21 23:00:47Z gojomo $
*
* Created on Oct 5, 2004
*
* Copyright (C) 2004 Internet Archive.
*
* This file is part of the Heritrix web crawler (crawler.archive.org).
*
* Heritrix is free software; you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation; either version 2.1 of the License, or
* any later version.
*
* Heritrix is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
*
* You should have received a copy of the GNU Lesser Public License
* along with Heritrix; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/ 
package tt;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.URIException;
import org.archive.crawler.datamodel.CandidateURI;
import org.archive.crawler.framework.CrawlController;
import org.archive.crawler.frontier.QueueAssignmentPolicy;
import org.archive.net.UURI;
import org.archive.net.UURIFactory;
public class ELFHashQueueAssignmentPolicy extends QueueAssignmentPolicy {
    private static final Logger logger = Logger.getLogger(ELFHashQueueAssignmentPolicy.class.getName());
    private static String DEFAULT_CLASS_KEY = "default...";
    private static final String DNS = "dns";
    public String getClassKey(CrawlController controller, CandidateURI cauri) {
        String scheme = cauri.getUURI().getScheme();
        String candidate = null;
        try {
            if (scheme.equals(DNS)){
                if (cauri.getVia() != null) {
                	UURI viaUuri = UURIFactory.getInstance(cauri.flattenVia());
                    candidate = viaUuri.getAuthorityMinusUserinfo();
                    scheme = viaUuri.getScheme();
                } else {
                    candidate= cauri.getUURI().getReferencedHost();
                }
            } else {
            	String uri = cauri.getUURI().toString();
            	long hash = ELFHash(uri);
                candidate =  Long.toString(hash % 100);
            }
            if(candidate == null || candidate.length() == 0) {
                candidate = DEFAULT_CLASS_KEY;
            }
        } catch (URIException e) {
            logger.log(Level.INFO, "unable to extract class key; using default", e);
            candidate = DEFAULT_CLASS_KEY;
        }
        if (scheme != null && scheme.equals(UURIFactory.HTTPS)) {
            if (!candidate.matches(".+:[0-9]+")) {
                candidate += UURIFactory.HTTPS_PORT;
            }
        }
        return candidate.replace(':','#');
    }
    public static long ELFHash(String str) {  
        long hash = 0;  
        long x = 0;  
        for (int i = 0; i < str.length(); i++) {  
            hash = (hash << 4) + str.charAt(i);  
            if ((x = hash & 0xF0000000L) != 0) {  
                hash ^= (x >> 24);  
                hash &= ~x;  
            }  
        }  
        return (hash & 0x7FFFFFFF);  
    } 
}
