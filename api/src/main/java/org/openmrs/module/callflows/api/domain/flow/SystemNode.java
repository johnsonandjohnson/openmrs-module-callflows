/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.domain.flow;

/**
 * A system node. This captures all the processing that happens on the server
 * All properties of this node is in the base class, but it can be extended in future as desired
 * During JSON de-serialization, it's helpful to identify this node by a specific class and hence the existence of this class
 *
 * @author bramak09
 * @see org.openmrs.module.callflows.api.domain.flow.UserNode
 */
public class SystemNode extends Node {

}
