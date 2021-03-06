<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta content="no-cache" http-equiv="pragma"></meta>
<meta content="no-cache" http-equiv="cache-control"></meta>
<meta content="0" http-equiv="expires"></meta>
<meta content="IE=9" http-equiv="X-UA-Compatible"></meta>


<link href="CommonCSS.css" rel="stylesheet" type="text/css" />
<script src="jquery-1.11.3.js"></script>


<title>Simplify IoT</title>

<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script> -->

<script type="text/javascript">
	$(document).ready(function() {
		var uid = $('#userId1').val();
		$('#reset').click(function(e) {
			e.defaultPrevented;
			clearOnSave();
		});
		
		$('#user_prov_save').click(function(e)
				
	{
	var plan=$('#planType').find('option:selected').attr('value').split("~");
	$.ajax({
		type : 'GET',
		url : 'http://iotapp.cfapps.io/rest/userProvision',
		data : {
			userId : $('#p_userId').val(),
			planId : plan[0],
		},
		dataType :'text',
		success : userProvSuccess,
		error : userProvFailure
	});
			
	
	});
	
		$('#save').click(function(e) {
			$("#billDetail tbody").empty();		
			$.ajax({
				type : 'GET',
				url : 'http://iotapp.cfapps.io/rest/gadget',
				data : {
	                userId : $('#userIdMock').val(),
					location : $('#locationMock').val(), 
					deviceCategory : $('#deviceCategoryMock').val(),
					dataVolume : $('#dataVolumeMock').val(),
	            },
				contentType : 'application/json; charset=utf-8',
				dataType : 'json',
				success : OnGetMemberSuccess,
				error : OnGetMemberError
			});
		});
		$('#show_bill').click(function(e) {
			$("#billDetail tbody").empty();
			$.ajax({
				type : 'GET',
				url : 'http://iotapp.cfapps.io/rest/currentBill',
				data : {
	                userId : $('#userId1').val()
	            },
				contentType : 'application/json; charset=utf-8',
				dataType : 'json',
				success : OnGetMemberSuccess,
				error : OnGetMemberError
			});
		});

		$("#planType").on('change', function(){
			
			var PlanVal=$(this).find('option:selected').attr('value');
			if(PlanVal=="undefined")
				{
				$("#planDetails tbody").empty();
				}else{
					var plan=$(this).find('option:selected').attr('value').split("~");
					
					$("#planDetails tbody").empty();
					$("#planDetails tbody").append(
						"<tr class=\"trow\">" + "<td>HealthDevices"
						+ "</td>" + "<td>" + plan[1] + "</td>"+ "<td>" + plan[6] + "</td></tr>"+
						"<tr class=\"trow\">" + "<td>Gadgets"
						+ "</td>"+ "<td>" + plan[2] + "</td>"+ "<td>" + plan[7] + "</td></tr>"+
						"<tr class=\"trow\">" + "<td>Appliances"
						+ "</td>" + "<td>" + plan[3] + "</td>"+ "<td>" + plan[8] + "</td></tr>"+
						"<tr class=\"trow\">" + "<td>Others"
						+ "</td>"+ "<td>" + plan[4] + "</td>"+ "<td>" + plan[9] + "</td></tr>"+
						"<tr class=\"trow\">" + "<td>PlanCharges"
						+ "</td>"+ "<td>" + plan[5] + "</td></tr>"
					);
				}
		
		});
		
		$.ajax({
			type : 'GET',
			url : 'http://iotapp.cfapps.io/rest/planDetails',
			contentType : 'application/json; charset=utf-8',
			dataType : 'json',
			success : getPlanTypeSuccess,
			error : getPlanTypeFailure
		});
		
		
	});

	function getPlanTypeSuccess(data, status){
		var mySelect = $('#planType');
		mySelect.append($('<option></option>').val("undefined").html(""));
		$.each(data, function(val, text){
			mySelect.append(
					$('<option></option>').val(text.planId+"~"+text.HealthDevices+"~"+text.Gadgets+"~"+text.Appliances+"~"+text.Others+"~"+text.PlanCharges+"~"+text.HealthDevices_Rate
							+"~"+text.Gadgets_Rate+"~"+text.Appliances_Rate+"~"+text.Others_Rate).html(text.planId));
			
		});
	}
	
	function getPlanTypeFailure(data, status){
		alert("Failed");
	}
	
	function userProvSuccess(data, status){
		alert(data);
	}
	
	function userProvFailure(data, status){
		alert("Save Failed");
	}
		
		
	function OnGetMemberSuccess(data, status) {
		clearOnSave();
		$.each(data, function(i, obj) {
			$("#billDetail tbody").append(
					"<tr class=\"trow\">" + "<td>" + obj.deviceCategory
							+ "</td>" + "<td>" + obj.dataVolume + "</td>"
							+ "<td>" + obj.currentBillAmt + "</td>" + "</tr>");
		});
		$("#billDetail tbody tr:even").css("background-color", "#E0E0E0");
		//jQuery code will go here...
	}

	function OnGetMemberError(request, status, error) {
		//jQuery code will go here...
		alert("failure");
	}

	function clearOnSave() {
		$("#userIdMock").val("");
		$("#locationMock").val("");
		$("#deviceCategoryMock").val("");
		$("#dataVolumeMock").val("");
	}
</script>
</head>
<body>
	<p id="header">Simplify IoT: Data Logistics and Billing</p>

	<table align="center">
		<tr>
			<td class="tdata">
				<table border="1">
						<thead class="theader">
							<tr>
								<th colspan="2">User Provisioning</th>
							</tr>
						</thead>
						
						<tr>
							<td>User Id</td>
							<td><input type="text" name="p_userId" id="p_userId"></td>							
						</tr>
						<tr>
							<td>Plan Type</td>
							<td><select id="planType">
							</select></td>
						</tr>
						
						<tr>
							<td align="center" colspan="2"><input type="button" value="Submit" id="user_prov_save"></td>
						</tr>
				</table>
			</td>
			
			<td class="tdata">
				<table border="1" id="planDetails">
				<thead class="theader">
				<tr>
					<th colspan="3" align="center">Plan Details</th>
				</tr>
						<tr>
							<th>Device Category</th>
							<th>Data Limit (MB)</th>
							<th>Overage Charge</th>
							</tr>
							</thead>
				<tbody>
				
				</tbody>
				</table>
			</td>
			
		</tr>
	
		<tr>

			<td class="tdata">
				<form>
					<table border="1">
						<thead class="theader">
							<tr>
								<th colspan="2">Mock Data</th>
							</tr>
						</thead>

						<tr>
							<td>User Id</td>
							<td><input type="text" name="userId" id="userIdMock"></td>
						</tr>
						<tr>
							<td>Location</td>
							<td><input type="text" name="location" id="locationMock"></td>
						</tr>
						<tr>
							<td>Data Category</td>
							<td>
							<select id="deviceCategoryMock">
							  <option value="HealthDevices">HealthDevices</option>
							  <option value="Gadgets">Gadgets</option>
							  <option value="Appliances">Appliances</option>
							  <option value="Others">Others</option>
							</select>
							</td>
							<!-- <td><input type="text" name="deviceCategory" id="deviceCategory"></td> -->
							
						</tr>
						<tr>
							<td>Data Volume</td>
							<td><input type="text" name="dataVolume" id="dataVolumeMock"></td>
						</tr>
						<tr>
							<td align="right"><input type="button" value="Save" id="save"></td>
							<td><input type="button" value="Reset" id="reset"></td>

						</tr>
					</table>
				</form>
			</td>

			<td class="tdata">
				<form>
					<table border="1">
						<tr>
							<td>User Id</td>
							<td><input type="text" name="userId1" id="userId1"></td>
							<td><input type="button" value="Show Bill" id="show_bill"></td>
						</tr>
					</table>
				</form>
			</td>


			<td class="tdata">
				<table border="1" id="billDetail">
					<thead class="theader">
						<tr>
							<th>Device Category ID</th>
							<th>Data Volume (MB)</th>
							<th>Amount ($)</th>
						</tr>
					</thead>
					<tbody>

					</tbody>
				</table>
			</td>


		</tr>
	</table>

</body>
</html>