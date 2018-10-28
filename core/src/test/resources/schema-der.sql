create table floor_drawing(
  id INT GENERATED BY DEFAULT AS IDENTITY,
  name VARCHAR(255) NOT NULL,
  upd_date TIMESTAMP,
  text_size NUMBER,
  configuration CLOB,
  CONSTRAINT floor_drawing_pk PRIMARY KEY(id),
  CONSTRAINT floor_drawing_uk UNIQUE(name)
);
create table floor_area(
  drawing_id INT,
  area_id INT,
  area_name VARCHAR(255) NOT NULL,
  main_start INT,
  branch_start INT,
  area_readiness VARCHAR(255) NOT NULL,
  defect_count INT,
  area  CLOB,
  CONSTRAINT floor_area_pk PRIMARY KEY(drawing_id, area_id),
  CONSTRAINT floor_area_uk UNIQUE(drawing_id, area_name)
);


