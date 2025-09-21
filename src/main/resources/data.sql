INSERT INTO loan_rates (min_age, max_age, annual_rate)
VALUES
    (0, 25, 0.05),
    (26, 40, 0.03),
    (41, 60, 0.02),
    (61, 150, 0.04)
    ON CONFLICT (min_age, max_age)
DO UPDATE SET annual_rate = EXCLUDED.annual_rate;
