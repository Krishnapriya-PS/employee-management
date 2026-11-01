-- Reference data for error_codes. Codes must match errorcode.properties. INSERT IGNORE keeps restarts safe and does not overwrite edits made in the table.

INSERT IGNORE INTO error_codes (code, user_message, system_message, http_status) VALUES
(4400, 'Please correct the highlighted fields', 'Request validation failed', 400),
(4401, 'The request could not be understood', 'Malformed JSON or invalid enum value', 400),
(4404, 'Employee not found', 'No employee exists for the given id', 404),
(4409, 'An employee with this email already exists', 'Unique constraint on email violated', 409),
(5500, 'Database exception while performing the operation', 'DataAccessException from persistence layer', 500),
(5000, 'Something went wrong. Please try again later.', 'Unhandled exception', 500);