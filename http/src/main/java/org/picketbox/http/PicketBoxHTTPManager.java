package org.picketbox.http;

import javax.servlet.http.HttpServletRequest;

import org.picketbox.core.PicketBoxManager;
import org.picketbox.core.UserContext;

public interface PicketBoxHTTPManager extends PicketBoxManager {

    UserContext getUserContext(HttpServletRequest request);

}