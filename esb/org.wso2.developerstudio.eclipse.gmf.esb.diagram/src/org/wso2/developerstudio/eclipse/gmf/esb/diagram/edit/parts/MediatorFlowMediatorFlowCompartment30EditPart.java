package org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeCompartmentEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.DragDropEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateUnspecifiedTypeConnectionRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.draw2d.ui.figures.ConstrainedToolbarLayout;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.tooling.runtime.edit.policies.reparent.CreationEditPolicyWithCustomReparent;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.policies.MediatorFlowMediatorFlowCompartment30CanonicalEditPolicy;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.edit.policies.MediatorFlowMediatorFlowCompartment30ItemSemanticEditPolicy;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.EsbVisualIDRegistry;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.part.Messages;
import org.wso2.developerstudio.eclipse.gmf.esb.diagram.providers.EsbElementTypes;

/**
 * @generated
 */
public class MediatorFlowMediatorFlowCompartment30EditPart extends ShapeCompartmentEditPart {

	/**
	 * @generated
	 */
	public static final int VISUAL_ID = 7057;

	/**
	 * @generated
	 */
	public MediatorFlowMediatorFlowCompartment30EditPart(View view) {
		super(view);
	}

	/**
	 * @generated
	 */
	public String getCompartmentName() {
		return Messages.MediatorFlowMediatorFlowCompartment30EditPart_title;
	}

	/**
	 * @generated
	 */
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE,
				new MediatorFlowMediatorFlowCompartment30ItemSemanticEditPolicy());
		installEditPolicy(EditPolicyRoles.CREATION_ROLE, new CreationEditPolicyWithCustomReparent(
				EsbVisualIDRegistry.TYPED_INSTANCE));
		installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new DragDropEditPolicy());
		installEditPolicy(EditPolicyRoles.CANONICAL_ROLE,
				new MediatorFlowMediatorFlowCompartment30CanonicalEditPolicy());
	}

	/**
	 * @generated
	 */
	protected void setRatio(Double ratio) {
		if (getFigure().getParent().getLayoutManager() instanceof ConstrainedToolbarLayout) {
			super.setRatio(ratio);
		}
	}

	/**
	 * @generated
	 */
	public EditPart getTargetEditPart(Request request) {
		if (request instanceof CreateViewAndElementRequest) {
			CreateElementRequestAdapter adapter = ((CreateViewAndElementRequest) request).getViewAndElementDescriptor()
					.getCreateElementRequestAdapter();
			IElementType type = (IElementType) adapter.getAdapter(IElementType.class);
			if (type == EsbElementTypes.DropMediator_3491) {
				return this;
			}
			if (type == EsbElementTypes.PropertyMediator_3492) {
				return this;
			}
			if (type == EsbElementTypes.ThrottleMediator_3493) {
				return this;
			}
			if (type == EsbElementTypes.FilterMediator_3494) {
				return this;
			}
			if (type == EsbElementTypes.LogMediator_3495) {
				return this;
			}
			if (type == EsbElementTypes.EnrichMediator_3496) {
				return this;
			}
			if (type == EsbElementTypes.XSLTMediator_3497) {
				return this;
			}
			if (type == EsbElementTypes.SwitchMediator_3498) {
				return this;
			}
			if (type == EsbElementTypes.Sequence_3503) {
				return this;
			}
			if (type == EsbElementTypes.EventMediator_3504) {
				return this;
			}
			if (type == EsbElementTypes.EntitlementMediator_3505) {
				return this;
			}
			if (type == EsbElementTypes.ClassMediator_3506) {
				return this;
			}
			if (type == EsbElementTypes.SpringMediator_3507) {
				return this;
			}
			if (type == EsbElementTypes.ScriptMediator_3508) {
				return this;
			}
			if (type == EsbElementTypes.FaultMediator_3509) {
				return this;
			}
			if (type == EsbElementTypes.XQueryMediator_3510) {
				return this;
			}
			if (type == EsbElementTypes.CommandMediator_3511) {
				return this;
			}
			if (type == EsbElementTypes.DBLookupMediator_3512) {
				return this;
			}
			if (type == EsbElementTypes.DBReportMediator_3513) {
				return this;
			}
			if (type == EsbElementTypes.SmooksMediator_3514) {
				return this;
			}
			if (type == EsbElementTypes.SendMediator_3515) {
				return this;
			}
			if (type == EsbElementTypes.HeaderMediator_3516) {
				return this;
			}
			if (type == EsbElementTypes.CloneMediator_3517) {
				return this;
			}
			if (type == EsbElementTypes.CacheMediator_3518) {
				return this;
			}
			if (type == EsbElementTypes.IterateMediator_3519) {
				return this;
			}
			if (type == EsbElementTypes.CalloutMediator_3520) {
				return this;
			}
			if (type == EsbElementTypes.TransactionMediator_3521) {
				return this;
			}
			if (type == EsbElementTypes.RMSequenceMediator_3522) {
				return this;
			}
			if (type == EsbElementTypes.RuleMediator_3523) {
				return this;
			}
			if (type == EsbElementTypes.OAuthMediator_3524) {
				return this;
			}
			if (type == EsbElementTypes.AggregateMediator_3525) {
				return this;
			}
			if (type == EsbElementTypes.StoreMediator_3588) {
				return this;
			}
			if (type == EsbElementTypes.BuilderMediator_3591) {
				return this;
			}
			if (type == EsbElementTypes.CallTemplateMediator_3594) {
				return this;
			}
			if (type == EsbElementTypes.PayloadFactoryMediator_3597) {
				return this;
			}
			if (type == EsbElementTypes.EnqueueMediator_3600) {
				return this;
			}
			if (type == EsbElementTypes.URLRewriteMediator_3620) {
				return this;
			}
			if (type == EsbElementTypes.ValidateMediator_3623) {
				return this;
			}
			if (type == EsbElementTypes.RouterMediator_3628) {
				return this;
			}
			if (type == EsbElementTypes.ConditionalRouterMediator_3635) {
				return this;
			}
			if (type == EsbElementTypes.BAMMediator_3680) {
				return this;
			}
			if (type == EsbElementTypes.BeanMediator_3683) {
				return this;
			}
			if (type == EsbElementTypes.EJBMediator_3686) {
				return this;
			}
			if (type == EsbElementTypes.DefaultEndPoint_3609) {
				return this;
			}
			if (type == EsbElementTypes.AddressEndPoint_3610) {
				return this;
			}
			if (type == EsbElementTypes.FailoverEndPoint_3611) {
				return this;
			}
			if (type == EsbElementTypes.RecipientListEndPoint_3692) {
				return this;
			}
			if (type == EsbElementTypes.WSDLEndPoint_3612) {
				return this;
			}
			if (type == EsbElementTypes.NamedEndpoint_3660) {
				return this;
			}
			if (type == EsbElementTypes.LoadBalanceEndPoint_3613) {
				return this;
			}
			if (type == EsbElementTypes.APIResourceEndpoint_3674) {
				return this;
			}
			if (type == EsbElementTypes.AddressingEndpoint_3689) {
				return this;
			}
			if (type == EsbElementTypes.HTTPEndpoint_3709) {
				return this;
			}
			if (type == EsbElementTypes.TemplateEndpoint_3716) {
				return this;
			}
			if (type == EsbElementTypes.CloudConnector_3719) {
				return this;
			}
			if (type == EsbElementTypes.CloudConnectorOperation_3722) {
				return this;
			}
			if (type == EsbElementTypes.LoopBackMediator_3736) {
				return this;
			}
			if (type == EsbElementTypes.RespondMediator_3739) {
				return this;
			}
			if (type == EsbElementTypes.CallMediator_3742) {
				return this;
			}
			if (type == EsbElementTypes.DataMapperMediator_3761) {
				return this;
			}
			if (type == EsbElementTypes.FastXSLTMediator_3764) {
				return this;
			}
			if (type == EsbElementTypes.ForEachMediator_3780) {
				return this;
			}
			return getParent().getTargetEditPart(request);
		}
		if (request instanceof CreateUnspecifiedTypeConnectionRequest) {
			return getParent().getTargetEditPart(request);
		}
		return super.getTargetEditPart(request);
	}

}
