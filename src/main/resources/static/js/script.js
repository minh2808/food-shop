$(function(){

// User Register validation

	var $userRegister=$("#userRegister");

	$userRegister.validate({
		
		rules:{
			name:{
				required:true,
				lettersonly:true
			}
			,
			email: {
				required: true,
				space: true,
				email: true
			},
			mobileNumber: {
				required: true,
				space: true,
				numericOnly: true,

			},
			password: {
				required: true,
				space: true

			},
			confirmpassword: {
				required: true,
				space: true,
				equalTo: '#pass'

			},
			address: {
				required: true,
				all: true

			}
			
		},
		messages:{
			name:{
				required:'Yêu cầu điền',
				lettersonly:'Tên không hợp lệ'
			},
			email: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				email: 'Email không hợp lệ'
			},
			mobileNumber: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				numericOnly: 'Số điện thoại không hợp lệ',
			},

			password: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng'

			},
			confirmpassword: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				equalTo: 'Không trùng khớp'

			},
			address: {
				required: 'Yêu cầu điền',
				all: 'Không hợp lệ'

			}
		}
	})
	
	
// Orders Validation

var $orders=$("#orders");

$orders.validate({
		rules:{
			firstName:{
				required:true,
				lettersonly:true
			},
			lastName:{
				required:true,
				lettersonly:true
			}
			,
			email: {
				required: true,
				space: true,
				email: true
			},
			mobileNo: {
				required: true,
				space: true,
				numericOnly: true,
				minlength: 10,
				maxlength: 12

			},
			address: {
				required: true,
				all: true

			},
			paymentType:{
			required: true
			}
		},
		messages:{
			firstName:{
				required:'Yêu cầu điền',
				lettersonly:'Không hợp lệ'
			},
			lastName:{
				required:'Yêu cầu điền',
				lettersonly:'Không hợp lệ'
			},
			email: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				email: 'Không hợp lệ'
			},
			mobileNo: {
				required: 'Yêu cầu điền',
				space: 'Không được có khoảng trắng',
				numericOnly: 'Không hợp lệ',
			}
		   ,
			address: {
				required: 'Yêu cầu điền',
				all: 'Không hợp lệ'

			},
			paymentType:{
			required: 'Chọn phương thức thanh toán'
			}
		}	
})

	
})



jQuery.validator.addMethod('lettersonly', function(value, element) {
		return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
	});
	
		jQuery.validator.addMethod('space', function(value, element) {
		return /^[^-\s]+$/.test(value);
	});

	jQuery.validator.addMethod('all', function(value, element) {
		return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
	});


	jQuery.validator.addMethod('numericOnly', function(value, element) {
		return /^[0-9]+$/.test(value);
	});