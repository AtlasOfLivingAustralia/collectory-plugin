package au.org.ala.collectory

/**
 * This concrete class allows access to actions that are common to all sub-classes of ProviderGroup, and
 * which are defined in the abstract ProviderGroupController.
 */
class EntityController extends ProviderGroupController {

    def index = { }

    protected ProviderGroup get(id) {
        return ProviderGroup._get(id)
    }

}
