# Roles

The collectory when deployed with CAS enabled supports the following authorisation roles.

## ROLE_ADMIN 
- Can do anything except GBIF registration which is a separate role. This role is typically used in lots of ALA components.

## ROLE_COLLECTION_ADMIN
- Can do anything except GBIF registration
- Limited to collectory scope

## ROLE_COLLECTION_EDITOR 
- Edit all metadata, but not data connection parameters 

## ROLE_GBIF_REGISTRATION
- GBIF registration role, for syncing with GBIF


## Granular Authorisation

The collectory also supports granular access for contacts associated with entity.

### Data providers
If a contact is listed again a data provider as an administrator, they can edit the data provider and any of its resources. This includes:
- Adding/removing contacts
- Managing sensitive data access
- Download access reports

### Institution
If a contact is listed again an institution as an administrator, they can edit the data provider and any of its resources. This includes:
- Adding/removing contacts
- Managing sensitive data access
- Download access reports

### Data resource
If a contact is listed again an data resource as an administrator, they can edit the data resource. This includes:
- Adding/removing contacts
- Managing sensitive data access
- Download access reports