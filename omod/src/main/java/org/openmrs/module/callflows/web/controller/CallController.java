package org.openmrs.module.callflows.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.module.callflows.api.contract.OutboundCallResponse;
import org.openmrs.module.callflows.api.domain.Call;
import org.openmrs.module.callflows.api.domain.CallFlow;
import org.openmrs.module.callflows.api.domain.Config;
import org.openmrs.module.callflows.api.domain.Constants;
import org.openmrs.module.callflows.api.domain.FlowPosition;
import org.openmrs.module.callflows.api.domain.Renderer;
import org.openmrs.module.callflows.api.domain.flow.Flow;
import org.openmrs.module.callflows.api.domain.flow.Node;
import org.openmrs.module.callflows.api.domain.types.CallDirection;
import org.openmrs.module.callflows.api.domain.types.CallStatus;
import org.openmrs.module.callflows.api.service.CallFlowService;
import org.openmrs.module.callflows.api.service.CallService;
import org.openmrs.module.callflows.api.service.ConfigService;
import org.openmrs.module.callflows.api.service.FlowService;
import org.openmrs.module.callflows.api.util.CallUtil;
import org.openmrs.module.callflows.api.util.DateUtil;
import org.openmrs.module.callflows.api.util.FlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Call Controller
 *
 * @author bramak09
 */
@Api(
    value = "Call",
    tags = {"REST API to handle inbound and outbound calls"})
@Controller
@RequestMapping("/callflows")
public class CallController extends RestController {

  private static final Log LOGGER = LogFactory.getLog(CallController.class);

  private static final String ROOT_LOGGER = "root";

  /**
   * All user request parameters are stored under this key in the velocity template. To access a
   * parameter named pin for instance, use $params.pin The $params MUST be considered as a RESERVED
   * word and must be avoided by the callflow designers to set their own vars.
   */
  private static final String KEY_PARAMS = "params";

  /**
   * All internal parameters necessary to the handling of the call are stored under this key For
   * instance, the callId can be accessed as $internal.callId The $internal prefix MUST be
   * considered as a reserved word and must be avoided by the callflow designers to set their own
   * vars. Sometimes it's necessary to pass these parameters also as request parameters, in which
   * case they need to be passed as internal.parameter, eg: internal.callId
   */
  private static final String KEY_INTERNAL = "internal";

  /** The key to store the current call ID */
  private static final String KEY_CALL_ID = "callId";

  /**
   * The key to identify the URL to submit back This is also known as the continuation URL, i.e the
   * URL required to continue the call
   */
  private static final String KEY_NEXT_URL = "nextURL";

  /**
   * The key under which the base URL will be available. This can for instance be used to construct
   * other known URLS like the call continuation URL, CMS-Lite URL, etc
   */
  private static final String KEY_BASE_URL = "baseURL";

  /** The key under which the call direction is indicated, either of OUTGOING or INCOMING */
  private static final String KEY_DIRECTION = "callDirection";

  /** Separator which will be used for played messages */
  private static final String SEPERATOR_MESSAGE = "|";

  private static final String FILE_NAME_INITIALS = "cfl_calls_";
  private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
  private static final String ATTACHMENT_FILENAME = "attachment; filename=";
  private static final String EXTENSION_ZIP = ".zip";
  private static final String ZIP_FILENAME = "calls_reference";
  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  private static final int DEFAULT_FETCH_SIZE = 10000;
  private static final int NUMBER_OF_TEN_K_FILES = 5;
  private static final String TELEPHONE_NUMBER = "Telephone Number";
  private static final String CALLFLOW_ENDED_STATUSES_GP_KEY = "messages.statusesEndingCallflow";

  @Autowired
  @Qualifier("callflows.configService")
  private ConfigService configService;

  @Autowired
  @Qualifier("callflows.callFlowService")
  private CallFlowService callFlowService;

  @Autowired
  @Qualifier("callflows.callService")
  private CallService callService;

  @Autowired
  @Qualifier("callflows.flowService")
  private FlowService flowService;

  @Autowired
  @Qualifier("callflows.flowUtil")
  private FlowUtil flowUtil;

  @Autowired
  @Qualifier("callflows.callUtil")
  private CallUtil callUtil;

  @PostConstruct
  public void initialize() {
    try {
      // The default Velocity.init creates a velocity.log file for logging
      // in the current directory of where the web server was started from
      // This might cause permission issues in some cases like a automated tool trying to restart
      // the server
      // So we reset this to use the existing logger rather than writing into velocity.log
      Properties props = new Properties();
      props.setProperty(
          RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, Log4JLogChute.class.getName());
      props.setProperty(Log4JLogChute.RUNTIME_LOG_LOG4J_LOGGER, ROOT_LOGGER);
      Velocity.init(props);
    } catch (Exception e) {
      throw new IllegalStateException(
          String.format("Error initializing template engine: %s", e.toString()), e);
    }
  }

  /**
   * REST API to handle a incoming call. This can also be the first point of entry once a outbound
   * call is invoked In case of a outbound call, a internal.callId parameter is mandatory and an
   * existing Call object will be retrieved using this param For a inbound call, a new call object
   * will be created and used
   *
   * @param request HttpServletRequest
   * @param conf the configuration to use, eg: voxeo
   * @param flowName the flow name to invoke
   * @param extension the extension to process. For IVR typically this would be vxml
   * @param params a map of parameters that are passed along with the request
   * @param headers a map of headers that are passed along with the request
   * @return
   */
  @ApiOperation(
      value = "Handles an incoming call",
      notes = "Handles an incoming call",
      response = ResponseEntity.class)
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successful completion of an incoming call"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_INTERNAL_ERROR,
            message = "Failure to handle an incoming call"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_BAD_REQUEST,
            message = "Passed an invalid configuration/flowName details")
      })
  @RequestMapping(value = "/in/{conf}/flows/{flowName}.{extension}", method = RequestMethod.GET)
  @SuppressWarnings("PMD.ExcessiveMethodLength")
  public ResponseEntity<String> handleIncoming(
      HttpServletRequest request,
      @ApiParam(name = "conf", value = "The configuration to use", required = true)
          @PathVariable(value = "conf")
          String conf,
      @ApiParam(name = "flowName", value = "The flow name to invoke", required = true)
          @PathVariable(value = "flowName")
          String flowName,
      @ApiParam(name = "extension", value = "the extension to process", required = true)
          @PathVariable(value = "extension")
          String extension,
      @ApiParam(
              name = "params",
              value = "A map of parameters that are passed along with the request")
          @RequestParam
          Map<String, String> params,
      @ApiParam(name = "headers", value = "A map of headers that are passed along with the request")
          @RequestHeader
          Map<String, String> headers) {

    LOGGER.debug(
        String.format("handleIncoming(name=%s, params=%s, headers=%s", flowName, params, headers));

    // Typically we won't get this parameter for inbound calls,
    // but when the system makes a outbound call, it creates a call record and then enters here with
    // a callId
    // Since this is the entry point for both inbound and outbound calls, we try to read this
    // parameter here
    String callId = params.get(KEY_CALL_ID);

    // The requested configuration from the URL
    Config config = null;

    // velocity context
    VelocityContext context = null;

    // captures any exception that happens.
    // If there is a exception, the response body contains the error rather than the evaluated
    // template, because, well, ahem we don't have any content generated successfully.
    Exception error = null;

    // final output string as a result of evaluating one/more templates. Can also be a JSON string
    // if extension was json
    String output = null;

    // current node being evaled
    Node currentNode = null;

    // current call object
    Call call = null;

    try {
      // load configuration first, cause it has the OSGI services to load. This throws
      // IllegalArgument if can't load config
      config = configService.getConfig(conf);

      // initialize velocity context next. Can throw IllegalState if some OSGI services could not be
      // loaded
      context = initContext(config, params);

      // load call flow. Throws IllegalArgument if can't find flow
      CallFlow startCallFlow = callFlowService.findByName(flowName);

      // load the flow object. Throws IllegalArgument if can't load flow
      Flow flow = flowService.load(startCallFlow.getName());

      // get the entry/first step of this node
      currentNode = flow.getNodes().get(0);

      if (callId == null) {
        // Create a new incoming Call
        call =
            callService.create(
                conf, startCallFlow, currentNode.getStep(), CallDirection.INCOMING, null);
      } else {
        // we arrived here from a outbound Call
        call = callService.findByCallId(callId);

        if (call == null) {
          throw new IllegalArgumentException(
              String.format("Call with id %s cannot be found", callId));
        }
        if (CallDirection.INCOMING.equals(call.getDirection())) {
          // it happens when we use ccxml extension which redirects to vxml. We should never use
          // context from ccxml,
          // it can cause a lot of problems, it is a reason why context has been reset.
          call.setContext(new HashMap<>());
        } else {
          // merge back
          callUtil.mergeCallWithContext(call, context);
        }
      }

      // Link the nextURL to submit the conversation to
      setInInternalContext(
          context, KEY_NEXT_URL, callUtil.buildContinuationUrl(request, call, extension));
      setInInternalContext(context, KEY_CALL_ID, call.getCallId());
      setInInternalContext(context, KEY_BASE_URL, callUtil.buildBaseUrl(request));
      setInInternalContext(context, KEY_DIRECTION, call.getDirection().name());

      // eval the template
      output = flowUtil.evalNode(flow, currentNode, context, extension);

      // We are working in a pure state-less model, so everything that is needed for the next
      // interaction must be
      // persisted in the database, so that everything works seamlessly, especially in a clustered
      // setup
      // Merge the output of variables set in velocity templates using #set into the call instance's
      // context
      callUtil.mergeContextWithCall(context, call);
      // We add status even though it will be updated via a CCXML status handler in order to update
      // status
      // if someone doesn't use the CCXML handler
      call.setStatus(CallStatus.IN_PROGRESS);
      // When-ever we come to the entry point, we have to reset the the end flow and the end-node to
      // where we are
      // This might be important for cases where we created the call elsewhere, but the entry point
      // is a different point
      // and hence we need to continue from here again
      call.setEndFlow(startCallFlow);
      call.setEndNode(currentNode.getStep());

      // By default we don't persist the params in the database, as that's coming from the user
      // and that's all potentially PII (Personally Identifiable) data
      // So if anything in params needs to be persisted across calls, it has to be #set in the
      // templates specifically
      // by the callflow designer
      callService.update(call);

    } catch (Exception e) {
      error = e;
      LOGGER.error(
          String.format(
              "ERROR has been caused by request: \n"
                  + "handleIncoming(name=%s, params=%s, headers=%s",
              flowName, params, headers));
      handleError(call, error);
    }
    return buildOutput(error, output, currentNode, call, extension, config);
  }

  /**
   * REST API to handle a call continuation. After a inbound or outbound call has run through
   * handleIncoming method once, all subsequent requests for that particular call will hit this API
   * right through to call termination The URL is always the same as it is differentiated by only
   * the unique callId The system remembers in which the node the user is currently at and handles
   * subsequent interactions accordingly
   *
   * @param request HttpServletRequest
   * @param callId the current call's unique identifier
   * @param extension the extension to process. For IVR typically this would be vxml
   * @param params a map of parameters that are passed along with the request
   * @param headers a map of headers that are passed along with the request
   * @return
   */
  @ApiOperation(
      value = "Handles call continuation",
      notes = "Handles call continuation",
      response = ResponseEntity.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Successful call continuation"),
        @ApiResponse(
            code = HttpURLConnection.HTTP_INTERNAL_ERROR,
            message = "Failure in call continuation due to cyclic loop")
      })
  @RequestMapping(value = "/calls/{callId}.{extension}", method = RequestMethod.GET)
  @SuppressWarnings("PMD.ExcessiveMethodLength")
  public ResponseEntity<String> handleContinuation(
      HttpServletRequest request,
      @ApiParam(name = "callId", value = "Current call's unique identifier", required = true)
          @PathVariable(value = "callId")
          String callId,
      @ApiParam(name = "extension", value = "The extension to process", required = true)
          @PathVariable(value = "extension")
          String extension,
      @ApiParam(
              name = "params",
              value = "A map of parameters that are passed along with the request")
          @RequestParam
          Map<String, String> params,
      @ApiParam(name = "headers", value = "A map of headers that are passed along with the request")
          @RequestHeader
          Map<String, String> headers) {

    LOGGER.debug(
        String.format(
            "handleContinuation(callId=%s, params=%s, headers=%s, extension=%s",
            callId, params, headers, extension));

    // The requested configuration from the URL
    Config config = null;
    // velocity context
    VelocityContext context = null;
    // captures any exception that happens.
    Exception error = null;
    // final output string as a result of evaluating one/more templates. Can also be a JSON string
    // if extension was json
    String output = null;
    // current node being evaled
    Node currentNode = null;
    // current call object
    Call call = null;

    try {
      // load current call here persisted from the last persistence
      call = callService.findByCallId(callId);

      if (!isCallCompleted(call)) {
        // The configuration is part of the call, and hence why we need to retrieve the call first
        config = configService.getConfig(call.getConfig());

        context = initContext(config, params);
        updateValueOfNextURL(request, extension, context, call);

        // merge back
        callUtil.mergeCallWithContext(call, context);

        // Load flow object
        Flow flow = flowService.load(call.getEndFlow().getName());

        String jumpTo = params.get(Constants.PARAM_JUMP_TO);
        if (!StringUtils.isBlank(jumpTo)) {
          // See if there is some jump to some other flow
          flow = flowService.load(jumpTo);
          currentNode = flow.getNodes().get(0);
        } else {
          // Go to next node of where control last terminated, this would be a system node since nodes
          // are in pairs
          currentNode = flowUtil.getNextNodeByStep(flow, call.getEndNode());
        }

        // evaluate node sequentially across jumps to arrive at a position
        // The position we arrived at will be a userNode or a systemNode
        // IF it's a system node it's normally a error and call will also terminate
        FlowPosition position = flowService.evalNode(flow, currentNode, context);

        output = position.getOutput();
        currentNode = position.getEnd();

        call.setEndFlow(callFlowService.findByName(position.getEndFlow().getName()));
        call.setEndNode(currentNode.getStep());

        // retrieve existing played messages
        String playedMessages = call.getPlayedMessages();

        // update the messages played, include the '|' symbol in the code, to provide flexibility to
        // submit the data after each node
        // update the played messages only when the data coming in as part of params
        if (StringUtils.isNotBlank(params.get(Constants.PARAM_PLAYED_MESSAGES))) {
          call.setPlayedMessages(
                  StringUtils.isNotBlank(playedMessages)
                          ? playedMessages
                          .concat(SEPERATOR_MESSAGE)
                          .concat(params.get(Constants.PARAM_PLAYED_MESSAGES))
                          : params.get(Constants.PARAM_PLAYED_MESSAGES));
        }

        LOGGER.debug("\nOn Setting playedMessages : " + call.getPlayedMessages());

        if (!position.isTerminated()) {
          // We are now back at a user node, so evaluate that again
          output = flowUtil.evalNode(flow, currentNode, context, extension);
        }
        call.setStatus(position.isTerminated() ? CallStatus.COMPLETED : call.getStatus());
        // one more interaction happened
        call.setSteps(call.getSteps() + 1);
        // merge everything back
        callUtil.mergeContextWithCall(context, call);
        // persist
        callService.update(call);
      }
    } catch (Exception e) {
      error = e;
      LOGGER.error(
          String.format(
              "ERROR has been caused by request: \n"
                  + "handleContinuation(callId=%s, params=%s, headers=%s",
              callId, params, headers));
      handleError(call, error);
    }
    return buildOutput(error, output, currentNode, call, extension, config);
  }

  /**
   * REST API to initiate a outbound call This is used only for adhoc testing purposes, and is not
   * intended to be used otherwise
   *
   * @param configName to use
   * @param name of the flow to invoke
   * @param extension to invoke
   * @param params a entry with key phone is required at the minimum
   * @return an empty string if call could not be created or a json with callId, status and
   *     statusText for debugging purposes
   */
  @ApiOperation(
      value = "Initiates an outbound call",
      notes = "Initiates an outbound call",
      response = OutboundCallResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successful completion of an outbound call")
      })
  @RequestMapping(value = "/out/{configName}/flows/{name}.{extension}", method = RequestMethod.GET)
  @ResponseBody
  public OutboundCallResponse handleOutgoing(
      @ApiParam(name = "configName", value = "Configuration name to use", required = true)
          @PathVariable(value = "configName")
          String configName,
      @ApiParam(name = "name", value = "Name of the flow to invoke", required = true)
          @PathVariable(value = "name")
          String name,
      @ApiParam(name = "extension", value = "Extension to invoke", required = true)
          @PathVariable(value = "extension")
          String extension,
      @ApiParam(
              name = "params",
              value = "A map of parameters that are passed along with the request")
          @RequestParam
          Map<String, Object> params) {
    LOGGER.debug(
        String.format(
            "handleOutgoing(config=%s, name = %s, extension=%s, params=%s",
            configName, name, extension, params));
    Call call = callService.makeCall(configName, name, params);
    return call != null ? new OutboundCallResponse(call) : null;
  }

  @ApiOperation(
      value = "Handles an outgoing call by PersonUuid",
      notes = "Handles an outgoing call by PersonUuid")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successful completion of an outbound call by PersonUuid")
      })
  @RequestMapping(
      value = "/person/{personUuid}/out/{configName}/flows/{name}.{extension}",
      method = RequestMethod.GET)
  @ResponseStatus(value = HttpStatus.OK)
  public void handleOutgoingByPersonUuid(
      @ApiParam(name = "configName", value = "Configuration name to use", required = true)
          @PathVariable(value = "configName")
          String configName,
      @ApiParam(name = "name", value = "Name of the flow to invoke", required = true)
          @PathVariable(value = "name")
          String name,
      @ApiParam(name = "extension", value = "Extension to invoke", required = true)
          @PathVariable(value = "extension")
          String extension,
      @ApiParam(name = "personUuid", value = "Person Uuid", required = true)
          @PathVariable(value = "personUuid")
          String personUuid,
      @ApiParam(
              name = "params",
              value = "A map of parameters that are passed along with the request")
          @RequestParam
          Map<String, Object> params) {
    Person person = Context.getPersonService().getPersonByUuid(personUuid);
    String phoneNumber = getTelephoneFromPerson(person);
    if (StringUtils.isNotBlank(phoneNumber)) {
      Map<String, Object> additionalParams = new HashMap<>();
      additionalParams.put(Constants.PARAM_PHONE, phoneNumber);
      additionalParams.put(Constants.PARAM_PERSON_ID, person.getPersonId());

      params.remove("returnUrl");
      additionalParams.putAll(params);

      callService.makeCall(configName, name, additionalParams);

    } else {
      throw new IllegalArgumentException(
          String.format("Missing phone number for %s person", personUuid));
    }
  }

  @ApiOperation(
      value = "Export calls details",
      notes = "Export calls details",
      response = Map.class)
  @ApiResponses(
      value = {
        @ApiResponse(
            code = HttpURLConnection.HTTP_OK,
            message = "Successfully exported calls details")
      })
  @RequestMapping(value = "/calls/export-details", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> exportCallsDetails(
      @ApiParam(name = "set", value = "Number of set")
          @RequestParam(defaultValue = "1", value = "set")
          Integer set,
      HttpServletResponse response)
      throws IOException {

    response.setHeader(
        HEADER_CONTENT_DISPOSITION, ATTACHMENT_FILENAME + ZIP_FILENAME + EXTENSION_ZIP);
    response.addHeader(HEADER_CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    // Logic: Fetching of data is divided into sets of 50,000 records, which should be downloaded as
    // 'cfl_references.zip' file.
    // Every zip contains 5 .csv files with 10,000 records each.
    long totalNumberOfRecords = callService.retrieveCount();
    int allowedNumberOfSets =
        (int) ((totalNumberOfRecords / (NUMBER_OF_TEN_K_FILES * DEFAULT_FETCH_SIZE)) + 1);

    List<Call> outboundCalls = null;
    Path tempFiles = Files.createTempDirectory(null);
    String tempDir = tempFiles.toString();

    Map<String, String> fileNames = new HashedMap(5);
    String currentFileName = null;
    if (0 < set && set <= allowedNumberOfSets && totalNumberOfRecords > 0) {
      for (int i = 1; i <= NUMBER_OF_TEN_K_FILES; i++) {
        outboundCalls = callService.findAll(i + (5 * (set - 1)), DEFAULT_FETCH_SIZE);
        if (!outboundCalls.isEmpty()) {
          currentFileName = FILE_NAME_INITIALS + i;
          fileNames.put(currentFileName, callUtil.generateFileName(tempDir, i));
          callUtil.generateReports(fileNames.get(currentFileName), outboundCalls);
        }
      }
      if (!fileNames.isEmpty()) {
        callUtil.createZip(response, fileNames);
        callUtil.deleteTempFile(tempDir, fileNames);
      }
    }

    return null;
  }

  private ResponseEntity buildOutput(
      Exception error, String output, Node node, Call call, String extension, Config config) {
    Renderer renderer = null;
    if (configService.hasRenderer(extension)) {
      renderer = configService.getRenderer(extension);
    }
    // We want very fine grained control over the final responses as they vary with extension,
    // errors, content, etc
    // So here we control all aspects of the output rather than deferring to the RestController
    return new ResponseEntity<String>(
        callUtil.buildOutput(error, output, node, call, extension),
        callUtil.buildHeaders(error, extension, config, renderer),
        callUtil.buildStatus(error));
  }

  private void handleError(Call call, Exception error) {
    LOGGER.error(error.toString(), error);
    // Say config/flow was not loaded yet, we don't have a call record in such cases
    if (null != call) {
      call.setStatus(CallStatus.FAILED);
      callService.update(call);
    }
  }

  private VelocityContext initContext(Config config, Map<String, String> params) {
    VelocityContext context = new VelocityContext();
    // put a internal key in the context for the application's needs
    // The internal keyword is reserved in all velocity templates for the app's use
    // The rest of the namespace is free for the callflow designer to use as desired
    context.put(KEY_INTERNAL, new HashMap<String, String>());

    // Some classes have very useful static methods that are useful when designing callflows
    // These are included here in upper case as variables will predominantly be in lower case
    // These are included only for convenience
    context.put("String", String.class);
    context.put("Integer", Integer.class);
    context.put("Long", Long.class);
    context.put("Float", Float.class);
    context.put("Double", Double.class);
    context.put("Date", Date.class);
    context.put("SimpleDateFormat", SimpleDateFormat.class);
    context.put("Calendar", Calendar.class);
    context.put("DateUtil", DateUtil.class);
    context.put("Math", Math.class);

    loadParams(context, params);
    loadBeans(context, config.getServicesMap());
    return context;
  }

  private void setInInternalContext(VelocityContext context, String key, Object value) {
    Map internal = (Map<String, String>) context.get(KEY_INTERNAL);
    internal.put(key, value);
  }

  private void loadParams(VelocityContext context, Map<String, String> params) {
    // add request parameters to the context under a certain key, so there are no conflicts with
    // local vars
    context.put(KEY_PARAMS, params);
  }

  private void loadBeans(VelocityContext context, Map<String, String> bundlesToLoad) {
    StringBuilder notFoundServices = new StringBuilder();

    for (Map.Entry<String, String> entry : bundlesToLoad.entrySet()) {
      Object service =
          ServiceContext.getInstance().getApplicationContext().getBean(entry.getValue());
      if (service != null) {
        context.put(entry.getKey(), service);
      } else {
        notFoundServices.append(entry.getValue());
        notFoundServices.append("\n");
      }
    }
    // We couldn't find some services
    if (!notFoundServices.toString().isEmpty()) {
      throw new IllegalStateException(
          String.format("Didn't load some services %s", notFoundServices));
    }
  }

  private void updateValueOfNextURL(
      HttpServletRequest request, String extension, VelocityContext context, Call call) {
    Map<String, Object> callContext = call.getContext();
    String nextUrlValue = callUtil.buildContinuationUrl(request, call, extension);
    if (callContext != null) {
      Map internal = (Map<String, String>) callContext.get(KEY_INTERNAL);
      if (internal != null) {
        internal.put(KEY_NEXT_URL, nextUrlValue);
      }
    } else {
      setInInternalContext(context, KEY_NEXT_URL, nextUrlValue);
    }
  }

  private String getTelephoneFromPerson(Person person) {
    String phoneNumber = null;
    if (person != null) {
      PersonAttribute telephoneAttribute = person.getAttribute(TELEPHONE_NUMBER);
      if (telephoneAttribute != null) {
        phoneNumber = telephoneAttribute.getValue();
      }
    }
    return phoneNumber;
  }

  private boolean isCallCompleted(Call call) {
    List<String> callflowEndedStatuses = Arrays.asList(Context.getAdministrationService()
            .getGlobalProperty(CALLFLOW_ENDED_STATUSES_GP_KEY)
            .split(","));

    return callflowEndedStatuses.contains(call.getStatus().name());
  }
}
