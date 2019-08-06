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
