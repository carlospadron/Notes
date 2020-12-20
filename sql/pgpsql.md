# PL/pgPSQL

## triggers
### trigger function
```sql
CREATE OR REPLACE FUNCTION myfunction() RETURNS trigger AS $$
BEGIN
  NEW.col1 = val1;
  NEW.col2 = (SELECT val2 FROM table 2);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```
### create trigger
```sql
CREATE TRIGGER myfunction
BEFORE INSERT OR UPDATE ON schema1.table1
FOR EACH ROW EXECUTE PROCEDURE myfunction(); 
```
