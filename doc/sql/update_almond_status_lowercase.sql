UPDATE almond_item
SET almond_status = LOWER(almond_status)
WHERE almond_status <> LOWER(almond_status);

UPDATE almond_state_log
SET from_status = LOWER(from_status)
WHERE from_status IS NOT NULL
  AND from_status <> LOWER(from_status);

UPDATE almond_state_log
SET to_status = LOWER(to_status)
WHERE to_status <> LOWER(to_status);
