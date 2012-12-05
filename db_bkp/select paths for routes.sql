select distinct r.route_id, t.shape_id, p.num_points
from routes r 
inner join trips t on r.route_id = t.route_id
inner join paths p on p.shape_id = t.shape_id